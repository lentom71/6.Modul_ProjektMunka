package covidVaccination.userinterface;

import covidVaccination.service.Citizen;
import covidVaccination.service.Registration;

import java.sql.SQLException;
import java.util.Scanner;

public class Regist
{
    private Registration service = new Registration();
    private Scanner scanner = new Scanner(System.in);

    public void registrate() throws SQLException {

        String name = askForName();
        String postalCode = askForPostalCode();
        int age = askForAge();
        String email = askForEmail1();
        askForEmail2(email);
        String ssn = askForSsn();

        checkIfThisPersonIsRegistratedBefore(ssn);
        service.writeIntoDatabase(new Citizen(name, postalCode, age, email, ssn));

        System.out.println();
        System.out.println("Regisztrációja sikeresen megtörtént");
    }

    private String askForName() {
        String name = "";
        int count = 1;
        while (count <= 3) {
            System.out.println("Kérem, adja meg a nevét!");
            name = scanner.nextLine();
            if (service.checkName(name)) {
                count = 4;
            } else if (count == 3) {
                throw new IllegalArgumentException("Több próbálkozás nem lehetséges.");
            } else {
                count++;
                System.out.println("A név nem lehet üres! (Összesen 3 próbálkozás lehetséges.)");
            }
        }
        return name;
    }

    private String askForPostalCode() throws SQLException {
        String postalCode = "";
        int count = 1;
        while (count <= 3) {
            System.out.println("Kérem, adja meg a lakóhelye irányítószámát!");
            postalCode = scanner.nextLine();
            if (!service.checkPostalCode(postalCode)) {
                throw new IllegalArgumentException("Az irányítószám nem lehet üres!");
            }
            String town = service.getTown(postalCode);
            if (!town.isEmpty()) {
                count = 4;
                System.out.println(town);
            } else if (count == 3) {
                throw new IllegalArgumentException("Több próbálkozás nem lehetséges.");
            } else {
                count++;
                System.out.println("Nem létező irányítószám. (Összesen 3 próbálkozás lehetséges.)");
            }
        }
        return postalCode;
    }

    private int askForAge() {
        int age = 0;
        int count = 1;
        while (count <= 3) {
            System.out.println("Kérem, adja meg az életkorát!");
            try {
                age = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException nfe) {
                System.out.println("A megadott adat nem értelmezhető számként!");
            }
            if (service.checkAge(age)) {
                count = 4;
            } else if (count == 3) {
                throw new IllegalArgumentException("Több próbálkozás nem lehetséges.");
            } else {
                count++;
                System.out.println("Az életkornak 10 és 150 közé kell esnie! (Összesen 3 próbálkozás lehetséges.)");
            }
        }
        return age;
    }

    private String askForEmail1() {
        String email = "";
        int count = 1;
        while (count <= 3) {
            System.out.println("Kérem, adja meg a kapcsolattartásra használt e-mail címét!");
            email = scanner.nextLine();
            if (service.checkEmail(email)) {
                count = 4;
            } else if (count == 3) {
                throw new IllegalArgumentException("Több próbálkozás nem lehetséges.");
            } else {
                count++;
                System.out.println("Az e-mail cím nem érvényes! (Összesen 3 próbálkozás lehetséges.)");
            }
        }
        return email;
    }

    private void askForEmail2(String email) {
        int count = 1;
        while (count <= 3) {
            System.out.println("Kérem, adja meg ugyanazt az e-mail címet még egyszer:");
            String email2 = scanner.nextLine();
            if (service.checkEmail2(email, email2)) {
                count = 4;
            } else if (count == 3) {
                throw new IllegalArgumentException("Több próbálkozás nem lehetséges.");
            } else {
                count++;
                System.out.println("Az e-mail cím eltér! (Összesen 3 próbálkozás lehetséges.)");
            }
        }
    }

    private String askForSsn() throws SQLException {
        String ssn = "";
        int count = 1;
        while (count <= 3) {
            System.out.println("Kérem, adja meg a TAJ-számát!");
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

    private void checkIfThisPersonIsRegistratedBefore(String ssn) throws SQLException {
        if (service.checkIfThisPersonIsRegistratedBefore(ssn)) {
            throw new IllegalArgumentException("Ezzel a TAJ-számmal már regisztráltak az oltásra! Második regisztráció nem lehetséges!");
        }
    }
}
