package covidVaccination.userinterface;


import covidVaccination.service.Registration;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Kérem, válasszon az alábbi menüpontok közül:");
        System.out.println();
        System.out.println("1. Egyéni regisztráció");
        System.out.println("2. Csoportos regisztráció");
        System.out.println("3. Oltásra jelöltek generálása");
        System.out.println("4. Oltás bejegyzése");
        System.out.println("5. Oltás meghiúsulásának bejegyzése");
        System.out.println("6. Oltási statisztika");
        System.out.println("7. KILÉPÉS");

        String number = scanner.nextLine();

        if (number.equals("1"))
        {
            try {
                Regist regi = new Regist();
                regi.registrate();
            } catch (SQLException sqle) {
                throw new IllegalStateException("Can not connect to database.", sqle);
            }
        }

        else if (number.equals("2")) {
            try {
                RegistMore regiMore = new RegistMore();
                regiMore.registrateMass();
            } catch (IOException | NullPointerException e) {
                throw new IllegalStateException("Can not read or write file.", e);
            } catch (SQLException sqle) {
                throw new IllegalStateException("Can not connect to database.", sqle);
            }
        }

        else if (number.equals("3")) {
            System.out.println("Kérem az irányitószámot!");
            String postalcode = scanner.nextLine();
            try {
                Registration regist = new Registration();
                regist.generateVaccinationListByPostalCode(LocalDate.now(), postalcode);
            } catch (IOException | NullPointerException e) {
                throw new IllegalStateException("Can not read or write file.", e);
            } catch (SQLException sqle) {
                throw new IllegalStateException("Can not connect to database.", sqle);
            }
        }

        else if (number.equals("4")) {
            try {
                Vaccination vaccination = new Vaccination();
                vaccination.vaccinate(true);
            } catch (SQLException sqle) {
                throw new IllegalStateException("Can not connect to database.", sqle);
            }
        }

        else if (number.equals("5")) {
            try {
                Vaccination vaccination = new Vaccination();
                vaccination.vaccinate(false);
            } catch (SQLException sqle) {
                throw new IllegalStateException("Can not connect to database.", sqle);
            }
        }

        else if (number.equals("6")) {
            try {
                ReportStatistic generator = new ReportStatistic();
                generator.generateStatistics();
            } catch (IOException ioe) {
                throw new IllegalStateException("Can not read or write file.", ioe);
            } catch (SQLException sqle) {
                throw new IllegalStateException("Can not connect to database.", sqle);
            }
        }
        else if (number.equals("7"))
        {
            return;
        }
        else
        {
            System.out.println("Nem létező menüpont. Viszontlátásra!");
            return;
        }
    }
}
