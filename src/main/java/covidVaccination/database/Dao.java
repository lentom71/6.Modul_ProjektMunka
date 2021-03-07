package covidVaccination.database;

import covidVaccination.service.Citizen;
import covidVaccination.service.VacciState;
import covidVaccination.service.VacciType;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Dao
{
    private MariaDbDataSource dataSource;

    public Dao() {
        try {
            this.dataSource = new MariaDbDataSource();
            dataSource.setUrl("jdbc:mariadb://localhost:3307/covid?useUnicode=true");
            dataSource.setUser("covid");
            dataSource.setPassword("covid");
        } catch (SQLException sqle) {
            throw new IllegalStateException("Can not connect to database.", sqle);
        }
    }

    public String getTown(String postalCode) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("select telepules, telepulesresz from postal_codes where irsz = ?")) {
            stmt.setString(1, postalCode);
            return getTownName(stmt);
        }
    }

    private String getTownName(PreparedStatement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery()) {
            String townName = "";
            if (rs.next()) {
                townName = rs.getString(1);
                String partOfTown = rs.getString(2);
                if (!partOfTown.isBlank()) {
                    return (townName + ", " + partOfTown);
                }
            }
            return townName;
        }
    }

    public boolean checkIfThisPersonIsRegistratedBefore(String ssn) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("select * from citizens where taj = ?;")) {
            stmt.setString(1, ssn);
            try (ResultSet rs = stmt.executeQuery()) {
                return (rs.next());
            }
        }
    }

    public void writeIntoDatabase(Citizen person) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("insert into citizens(citizen_name, zip, age, email, taj, number_of_vaccination, last_vaccination) values (?, ?, ?, ?, ?, ?, ?);")) {
            stmt.setString(1, person.getName());
            stmt.setString(2, person.getPostalCode());
            stmt.setInt(3, person.getAge());
            stmt.setString(4, person.getEmail());
            stmt.setString(5, person.getSsn());
            stmt.setInt(6, VacciState.NO.getValue());
            stmt.setTimestamp(7, null);

            stmt.executeUpdate();
        }
    }


   public PriorityQueue<Citizen> getVaccinationList(String postalCode) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM citizens WHERE zip = ? ORDER BY age DESC, citizen_name ASC;")) {
            stmt.setString(1, postalCode);
            return getList(stmt);
        }
    }

    private PriorityQueue<Citizen> getList(PreparedStatement stmt) throws SQLException {
        PriorityQueue<Citizen> vaccinationList = new PriorityQueue();
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Citizen person = getPerson(rs);
                vaccinationList.add(person);
            }
        }
        return vaccinationList;
    }

    public Citizen getPersonWithThisSsn(String ssn) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("select * from citizens where taj = ?;")) {
            stmt.setString(1, ssn);
            return executeGetPerson(stmt);
        }
    }

    private Citizen executeGetPerson(PreparedStatement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return getPerson(rs);
            }
        }
        return null;
    }

    private Citizen getPerson(ResultSet rs) throws SQLException {

        long id = rs.getLong(1);
        String name = rs.getString(2);
        String postalCode = rs.getString(3);
        int age = rs.getInt(4);
        String email = rs.getString(5);
        String ssn = rs.getString(6);
        VacciState numberOfVaccinesGot = VacciState.getState(rs.getInt(7));

        Citizen person = new Citizen(id, name, postalCode, age, email, ssn, numberOfVaccinesGot);
        if (rs.getDate(8) != null) {
            person.setLastVaccination(rs.getDate(8).toLocalDate());
            person.setVaccineType(VacciType.valueOf(rs.getString(9)));
        }
        return person;
    }

    public void registrateVaccinationIntoDatabase(String ssn, VacciState numberOfVaccinesGot, LocalDate date, VacciType Type) throws SQLException {
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("UPDATE citizens SET number_of_vaccination = ?, last_vaccination = ?, vaccine_type = ? WHERE taj = ?;")) {
            stmt.setInt(1, numberOfVaccinesGot.getValue());
            stmt.setDate(2, Date.valueOf(date));
            stmt.setString(3, Type.toString());
            stmt.setString(4, ssn);
            stmt.executeUpdate();
        }
        registrateVaccination(ssn, numberOfVaccinesGot, date, Type);
    }

    private void registrateVaccination(String ssn, VacciState numberOfVaccinesGot, LocalDate date, VacciType vaccineType) throws SQLException {
        long id = getId(ssn);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("insert into vaccinations(citizen_id, taj, vaccination_date, number_of_vaccination, vaccine_type) values (?, ?, ?, ?, ?);")) {
            stmt.setLong(1, id);
            stmt.setString(2, ssn);
            stmt.setDate(3, Date.valueOf(date));
            stmt.setInt(4, numberOfVaccinesGot.getValue());
            stmt.setString(5, vaccineType.toString());

            stmt.executeUpdate();
        }
    }

    private long getId(String ssn) throws SQLException
    {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("select citizen_id from citizens where taj = ?;"))
        {
            stmt.setString(1, ssn);
            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next())
                {
                    return rs.getLong(1);
                }
            }
        }
        throw new IllegalArgumentException("Nincs iyen regisztráció a rendszerben!");
    }

    public void registrateFail(String ssn, String comments, String reason, LocalDate date) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("update citizens set comments = ? where taj = ?;"))
        {
            stmt.setString(1, comments);
            stmt.setString(2, ssn);

            stmt.executeUpdate();
        }
        registrateFailIntoVaccinations(ssn, reason, date);
    }

    private void registrateFailIntoVaccinations(String ssn, String reason, LocalDate date) throws SQLException {
        long id = getId(ssn);
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("insert into vaccinations(citizen_id, taj, vaccination_date, comments) values (?, ?, ?, ?);")) {
            stmt.setLong(1, id);
            stmt.setString(2, ssn);
            stmt.setDate(3, Date.valueOf(date));
            stmt.setString(4, reason);

            stmt.executeUpdate();
        }
    }

    public String getCommentsBefore(String ssn) throws SQLException
    {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("select comments from citizens where taj = ?;"))
        {
            stmt.setString(1, ssn);
            return getComments(stmt);
        }
    }

    private String getComments(PreparedStatement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery())
        {
            if (rs.next()) {
                return rs.getString(1);
            }
        }
        return "";
    }

    public List<Integer> getTownData(String postalCode) throws SQLException
    {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("select number_of_vaccination from citizens where zip = ?;"))
        {
            stmt.setString(1, postalCode);
            return townData(stmt);
        }
    }

    private List<Integer> townData(PreparedStatement stmt) throws SQLException {
        List<Integer> townData = new ArrayList<>();
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                townData.add(rs.getInt(1));
            }
        }
        return townData;
    }
}
