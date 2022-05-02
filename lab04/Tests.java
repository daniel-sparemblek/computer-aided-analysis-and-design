public class Tests {
    private static final double MUTATION_PROB_DOUBLE = 0.3;
    private static final double MUTATION_PROB_BINARY = 0.005;
    private static final double PRECISION = 0.0001;
    private static final int UPPER_LIMIT = 150;
    private static final int LOWER_LIMIT = -50;
    private static final int POPULATION = 100;
    private static final boolean VERBOSE = true;
    private static final boolean SINGLE_POINT_CRX = true;
    private static final boolean ARITHEMITIC_CRX = true;
    private static final int NUM_EVALUTIONS = 1000000;
    private static final boolean BINARY = true;

    public static void main(String[] args) {
        Functions f1 = new Functions(1);
        Functions f3 = new Functions(3);
        Functions f6 = new Functions(6);
        Functions f7 = new Functions(7);

        System.out.println("ZADATAK 1");
        // If changing between binary and double, change mutation probability!
        // Also specify what kind of crossover you're doing

//        GA GA0 = new GA(!BINARY, UPPER_LIMIT, LOWER_LIMIT,
//                3, POPULATION, 2, f1, MUTATION_PROB_DOUBLE,
//                NUM_EVALUTIONS, ARITHEMITIC_CRX, SINGLE_POINT_CRX, VERBOSE, PRECISION);
//        GA0.run();
//        f1.setNum_of_calls(0);
//        GA GA1 = new GA(!BINARY, UPPER_LIMIT, LOWER_LIMIT,
//                3, POPULATION, 5, f3, MUTATION_PROB_DOUBLE,
//                NUM_EVALUTIONS, ARITHEMITIC_CRX, SINGLE_POINT_CRX, VERBOSE, PRECISION);
//        GA1.run();
//        f3.setNum_of_calls(0);
//        GA GA2 = new GA(BINARY, UPPER_LIMIT, LOWER_LIMIT,
//                3, POPULATION, 2, f6, MUTATION_PROB_BINARY,
//                NUM_EVALUTIONS, ARITHEMITIC_CRX, SINGLE_POINT_CRX, VERBOSE, PRECISION);
//        GA2.run();
//        f6.setNum_of_calls(0);
//        GA GA3 = new GA(!BINARY, UPPER_LIMIT, LOWER_LIMIT,
//                3, POPULATION, 2, f7, MUTATION_PROB_DOUBLE,
//                NUM_EVALUTIONS, ARITHEMITIC_CRX, SINGLE_POINT_CRX, VERBOSE, PRECISION);
//        GA3.run();
//        f7.setNum_of_calls(0);
        // ********************************************************************************
        //                                      COMMENTS
        // Cini se da ako pokrecemo genetski algoritam koristeni prikaz s pomicnim zarezom,
        // tesko ce mu biti pronaci rjesenja za 6. i 7. funkciju, dok ce lakse biti pronaci
        // za 1. i 3. Ako pokrecemo algoritam s binarnim prikazom, situacija je obrnuta.
        // Jedan od mogucih razloga tome je lakse izlazenje iz lokalnog minimuma za binarni
        // prikaz
        //
        // ********************************************************************************




        System.out.println("ZADATAK 2");
        // Change dimension (1, 3, 5, 7)
//        GA GA4 = new GA(!BINARY, UPPER_LIMIT, LOWER_LIMIT,
//                3, POPULATION, 1, f6, MUTATION_PROB_DOUBLE,
//                NUM_EVALUTIONS, ARITHEMITIC_CRX, SINGLE_POINT_CRX, VERBOSE, PRECISION);
//        GA4.run();
//        f6.setNum_of_calls(0);
//        GA GA5 = new GA(!BINARY, UPPER_LIMIT, LOWER_LIMIT,
//                3, POPULATION, 1, f7, MUTATION_PROB_DOUBLE,
//                NUM_EVALUTIONS, ARITHEMITIC_CRX, SINGLE_POINT_CRX, VERBOSE, PRECISION);
//        GA5.run();
//        f7.setNum_of_calls(0);
        // ********************************************************************************
        //                                      COMMENTS
        // Povecanje dimenzionalnosti utjece na rjesenje na sljedeci nacin: sto je
        // dimenzionalnost veca, to je teze pronaci rjesenje.
        //
        // ********************************************************************************



        System.out.println("ZADATAK 3");
        // Change dimensions (3, 6)
//        GA GA6 = new GA(BINARY, UPPER_LIMIT, LOWER_LIMIT,
//                4, POPULATION, 3, f6, MUTATION_PROB_BINARY,
//                NUM_EVALUTIONS, ARITHEMITIC_CRX, SINGLE_POINT_CRX, VERBOSE, PRECISION);
//        GA6.run();
//        f6.setNum_of_calls(0);
//        GA GA7 = new GA(!BINARY, UPPER_LIMIT, LOWER_LIMIT,
//                3, POPULATION, 3, f7, MUTATION_PROB_DOUBLE,
//                NUM_EVALUTIONS, ARITHEMITIC_CRX, SINGLE_POINT_CRX, VERBOSE, PRECISION);
//        GA7.run();
//        f7.setNum_of_calls(0);
        // ********************************************************************************
        //                                      COMMENTS
        // Binarni prikaz nalazi rjesenje puno brze nego li to cini prikaz s pomicnim zarezom
        // Dok razlika izmedu f6 i f7 svodi se na to da je f7 "teza" funkcija, odnosno duze
        // treba da se kod nje pronade rjesenje
        //
        // ********************************************************************************



        System.out.println("ZADATAK 4");
        // Ideal parameters for f6
//        GA GA8 = new GA(BINARY, UPPER_LIMIT, LOWER_LIMIT,
//                3, 30, 2, f6, 0.3,
//                NUM_EVALUTIONS, ARITHEMITIC_CRX, SINGLE_POINT_CRX, VERBOSE, PRECISION);
//        GA8.run();
//        f6.setNum_of_calls(0);
        // ********************************************************************************
        //                                      COMMENTS
        // Prema box-plotovima (koje cu prikazati na usmenom ispitivanju, buduci da ne znam
        // kako bi ih ovdje ukljucio) vidljivo je da su idealni parametri sljedeci:
        // Velicina populacije: 30
        // Stopa mutacije: 0.1
        //
        // ********************************************************************************




        System.out.println("ZADATAK 5");
        // Change tournament selection
//        GA GA9 = new GA(!BINARY, UPPER_LIMIT, LOWER_LIMIT,
//                3, POPULATION, 3, f6, MUTATION_PROB_DOUBLE,
//                NUM_EVALUTIONS, ARITHEMITIC_CRX, SINGLE_POINT_CRX, VERBOSE, PRECISION);
//        GA9.run();
//        f6.setNum_of_calls(0);
        // ********************************************************************************
        //                                      COMMENTS
        // Rucnim mjenjanjem velicine turnira, dolazim do zakljucka da velicina turnira
        // ne utjece na brzinu pronalaska rjesenja, niti na bolji pronalazak rjesenja
        //
        // ********************************************************************************
    }
}
