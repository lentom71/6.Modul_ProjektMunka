package covidVaccination.service;

import java.time.LocalDate;

public class Validation
{
    public boolean checkName(String name) {
        return (name != null && !name.isEmpty() && !name.isBlank());
    }

    public boolean checkPostalCode(String postalCode) {
        return (postalCode != null && !postalCode.isEmpty() && !postalCode.isBlank());
    }

    public boolean checkAgeIsNumber(String ageString) {
        String digits = "0123456789";
        for (char c : ageString.toCharArray()) {
            if (digits.indexOf(c) < 0) {
                return false;
            }
        }
        return true;
    }

    public boolean checkAge(int age) {
        return (age > 10 && age < 150);
    }

    public boolean checkEmail(String email) {
        return (email.length() >= 3 && email.contains("@"));
    }

    public boolean checkEmail2(String email, String email2) {
        return (email.equals(email2));
    }

    public boolean checkSsn(String ssn) {
        if (ssn == null || ssn.isBlank() || ssn.isEmpty() || ssn.length() != 9) {
            return false;
        } else {
            return checkIfSsnContainsOnlyDigitsAndTheseAreValid(ssn);
        }
    }

    private boolean checkIfSsnContainsOnlyDigitsAndTheseAreValid(String ssn) {
        String digits = "0123456789";
        for (char c : ssn.toCharArray()) {
            if (digits.indexOf(c) < 0) {
                return false;
            }
        }
        return checkIfSsnContainsOnlyValidDigits(ssn);
    }

    private boolean checkIfSsnContainsOnlyValidDigits(String ssn) {
        int sum = 0;
        String[] digitsOfSsn = ssn.split("");
        for (int i = 1; i < 9; i++) {
            if (i % 2 == 1) {
                sum += Integer.parseInt(digitsOfSsn[i - 1]) * 3;
            } else {
                sum += Integer.parseInt(digitsOfSsn[i - 1]) * 7;
            }
        }
        int cdvCode = sum % 10;

        return (cdvCode == Integer.parseInt(digitsOfSsn[8]));
    }

    public boolean checkDate(LocalDate date1, LocalDate date2) {
        return date1 == null || date1.plusDays(15).isBefore(date2) || date1.plusDays(15).equals(date2);
    }
}
