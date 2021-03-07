package covidVaccination.service;

public enum VacciType
{
    PFIZER, MODERNA, ASTRA_ZENECA, SZPUTNYIK;

    public static VacciType getType(int number) {
        switch (number) {
            case 1 : return PFIZER;
            case 2 : return MODERNA;
            case 3 : return ASTRA_ZENECA;
            case 4 : return SZPUTNYIK;
            default: throw new IllegalArgumentException("Nem létező vakcinatípus!");
        }
    }
}
