package covidVaccination.userinterface;

import covidVaccination.service.Citizen;
import covidVaccination.service.Registration;
import covidVaccination.service.VacciState;
import covidVaccination.service.VacciType;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Vaccination
{
    private Registration service = new Registration();
    private Scanner scanner = new Scanner(System.in);

    public void vaccinate(boolean gotVaccine) throws SQLException {
        if (gotVaccine == true) {
            registrateVaccination();
        } else {
            registrateFailedVaccination();
        }
    }

    private void registrateVaccination() throws SQLException {
        System.out.println("Az oltás bejegyzéséhez kérem, adja meg a következő adatokat:");
        String ssn = askForSsn();
        Citizen person = service.getPersonWithThisSsn(ssn);
        LocalDate date = askForDate();
        getImportantVaccinationInformationForThisPerson(person, date);
        VacciType vaccineType = askForVaccineType(person);

        service.registrateVaccinationIntoDatabase(ssn, person.getNumberOfVaccinesGot(), date, vaccineType);

        System.out.println();
        System.out.println("Az adatok rögzítése sikeresen megtörtént");
    }

    private void registrateFailedVaccination() throws SQLException {
        System.out.println("Az oltás meghiúsulásának bejegyzéséhez kérem, adja meg a következő adatokat:");
        String ssn = askForSsn();
        if (!service.checkIfThisPersonIsRegistratedBefore(ssn)) {
            throw new IllegalArgumentException("Ezzel a TAJ-számmal még nem regisztráltak az oltásra! A bejegyzés nem lehetséges!");
        } else {
            LocalDate date = askForDate();
            System.out.println("Írja le, milyen okból hiúsult meg az oltás:");
            String reason = scanner.nextLine();

            service.registrateFail(ssn, reason, date);
        }
        System.out.println();
        System.out.println("Az adatok rögzítése sikeresen megtörtént.");
    }

    private String askForSsn() throws SQLException {
        String ssn = "";
        int count = 1;
        while (count <= 3) {
            System.out.println("A személy TAJ-száma:");
            ssn = scanner.nextLine();
            if (service.checkSsn(ssn)) {
                count = 4;
            } else if (count == 3) {
                throw new IllegalArgumentException("Több próbálkozás nem lehetséges.");
            } else {
                count++;
                System.out.println("Nem érvényes a TAJ-szám! (Összesen 3 próbálkozás lehetséges.)");
            }
        }
        return ssn;
    }

    private void getImportantVaccinationInformationForThisPerson(Citizen person, LocalDate date) {
        if (person == null) {
            throw new IllegalArgumentException("Ezzel a TAJ-számmal még nem regisztráltak az oltásra! Az oltás nem lehetséges!");
        }

        if (person.getNumberOfVaccinesGot() == VacciState.NO) {
            System.out.println("Ehhez a TAJ-számhoz tartozó személynek az első oltás bejegyzése lehetséges.");
        } else if (person.getNumberOfVaccinesGot() == VacciState.ONE && service.checkDate(person, date)) {
            System.out.println("Ehhez a TAJ-számhoz tartozó személynek a második oltás bejegyzése lehetséges.");
            System.out.println("Az előző oltás típusa: " + person.getVaccineType().toString());
        } else if (person.getNumberOfVaccinesGot() == VacciState.ONE && !service.checkDate(person, date)) {
            System.out.println("Ehhez a TAJ-számhoz tartozó személy csak egy későbbi időpontban kaphatja meg a második adag vakcinát!");
            throw new IllegalArgumentException("Az oltás bejegyzése nem lehetséges!");
        } else {
            System.out.println("Ehhez a TAJ-számhoz tartozó személy már két adag vakcinát kapott.");
            throw new IllegalArgumentException("További oltás bejegyzése nem lehetséges!");
        }
    }

    private LocalDate askForDate() {
        int count = 0;
        LocalDate date = null;
        while (count <= 3) {
            System.out.println("Kérem, adja meg az oltás dátumát a következő formátumban: éééé-hh-nn");
            String dateString = scanner.nextLine();
            try {
                date = LocalDate.parse(dateString);
                count = 4;
            } catch (DateTimeParseException dtpe) {
                count++;
                if (count == 3) {
                    throw new IllegalArgumentException("Több próbálkozás nem lehetséges.");
                }
                System.out.println("Nem értelmezhető a dátum. (Összesen 3 próbálkozás lehetséges.)");
            }
        }
        return date;
    }

    private VacciType askForVaccineType(Citizen person) {
        if (person.getVaccineType() == null) {
            System.out.println("Kérem, adja meg az alábbi listából a beadott vakcina típusához tartozó számot:");
            System.out.println("1. PFIZER");
            System.out.println("2. MODERNA");
            System.out.println("3. ASTRA_ZENECA");
            System.out.println("4. SZPUTNYIK");

            int number;
            try {
                number = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("Nem létező vakcinatípus!");
            }
            return VacciType.getType(number);
        } else {
            System.out.println("Ez a személy csak a " + person.getVaccineType().toString() + " vakcinát kaphatja.");
            return person.getVaccineType();
        }
    }
}
