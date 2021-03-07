package covidVaccination.userinterface;

import covidVaccination.service.Registration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Scanner;

public class ReportStatistic
{
    private Registration service = new Registration();
    private Scanner scanner = new Scanner(System.in);

    public void generateStatistics() throws SQLException, IOException {
        System.out.println("Egy adott településre vonatkozó statisztika");
        String statMode = scanner.nextLine();

        if (!statMode.equals("1") && !statMode.equals("2")) {
            System.out.println("Nem létező menüpont.");
            return;
        }

        System.out.println();
        System.out.println("Válassza ki, milyen formában kéri a  statisztikát:");
        System.out.println();
        System.out.println("1. Ide, a felületre kiírva kérem az adatokat.");
        System.out.println("2. Fájlba kiírva kérem az adatokat a \"covid\" mappába. (Alapértelmezett)");
        String statForm = scanner.nextLine();

        if (statMode.equals("1") && statForm.equals("1")) {
            String postalCode = askForPostalCode();
            String town = service.getTown(postalCode);
            Map<String, Integer> townStatistics = service.getTownStatistics(postalCode);
            System.out.println("Oltási statisztika " + postalCode + ", " + town + " településre:");
            System.out.println(townStatistics);
        }

        else if (statMode.equals("1")) {
            String postalCode = askForPostalCode();
            service.writeTownStatisticsToFile(postalCode);
        }
    }

    private String askForPostalCode() throws SQLException {
        String postalCode = "";
        int count = 1;
        while (count <= 3) {
            System.out.println("Adja meg a település irányítószámát, melynek oltási adataira kíváncsi:");
            postalCode = scanner.nextLine();
            String town = service.getTown(postalCode);
            if (!town.isEmpty()) {
                count = 4;
            } else if (count == 3) {
                throw new IllegalArgumentException("Több próbálkozás nem lehetséges.");
            } else {
                count++;
                System.out.println("Nem létező irányítószám. (Összesen 3 próbálkozás lehetséges.)");
            }
        }
        return postalCode;
    }
}
