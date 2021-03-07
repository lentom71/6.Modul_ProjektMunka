package covidVaccination.userinterface;

import covidVaccination.service.Registration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class RegistMore
{
    private Registration service = new Registration();
    private Scanner scanner = new Scanner(System.in);

    public void registrateMass() throws IOException, SQLException, NullPointerException {

        System.out.println("Először töltse föl a fájlt a \"covid\" mappába, majd nyomja meg az Enter-t.");
        scanner.nextLine();
        System.out.println("Adja meg a fájl nevét, amely a regisztrálni kívánt emberek adatait tartalmazza!");
        String filename = scanner.nextLine();
        System.out.println("Adja meg a karaktert, amivel a fájlban az adatok el vannak választva!");
        String splitter = scanner.nextLine();

        List<String> wrongLines = service.registrateMass(filename, splitter);

        if (wrongLines.size() == 0) {
            System.out.println();
            System.out.println("Minden regisztráció sikeres volt.");
        } else {
            System.out.println();
            System.out.println("Néhány regisztráció sikertelen volt.");
            System.out.println("Kérem, válasszon a következő menüpontok közül:");
            System.out.println();
            System.out.println("1. Ide, a felületre kiírva kérem az adatokat.");
            System.out.println("2. Fájlba kiírva kérem az adatokat a \"covid\" mappába. (Alapértelmezett)");
            String number = scanner.nextLine();

            if (number.equals("1")) {
                for (String s : wrongLines) {
                    System.out.println(s);
                }
            } else {
                service.writeWrongLinesToFile(wrongLines);
            }
        }
    }
}
