import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class main2 {

    // Used for writing a String str to a given file in a csv format (must include ',')
    public static void writeToOutputFile(String str, String filename) throws Exception {
        fw = new FileWriter(filename, true);
        bw = new BufferedWriter(fw);
        pw = new PrintWriter(bw);

        pw.println(str);
        bw.close();
        pw.close();
        fw.close();
    }

    static FileWriter fw = null;
    static BufferedWriter bw = null;
    static PrintWriter pw = null;

    public static void main(String[] args) throws Exception {
        Scanner in = new Scanner(System.in);
        final int seed = 7;
        Random randint = new Random(seed); // declare a random object with seed
        int trialnum = 100; // number of trials/sets of cohorts
        int evol = 1000; // number of evolutions (if robustness is used)
        int num_patients = 10000; // default number of in-silico patients to be used
        ArrayList<Double> hour = new ArrayList<>(); // hour of treatment (current set at 6 am)
        hour.add(6.0); // 6 AM treatment
        boolean direct = true; // boolean for direct cell kill
        boolean indirect = !true; // boolean for indirect cell kill
        boolean d; // boolean for default parameters
        int selection = 1; // boolean/int for whether to sort robustness by error/J
        boolean robust_test; // whether robustness is to be used
        ArrayList<Cohort> datastore = new ArrayList<>(); // stores lambda/alpha/J statistic
        //double[] vals; // stores J Statistic and error for each patient

        ArrayList<Patient> allpts = new ArrayList<>(); // creates a blank arraylist (vector) of patient objects

        System.out.println("Default? (True / False)");
        String def = in.nextLine();
        while (!def.equalsIgnoreCase("true") && !def.equalsIgnoreCase("false")) {
            System.out.println("Try again.");
            def = in.next();
        }
        d = Boolean.parseBoolean(def);

        System.out.println("Robustness Testing? (True / False)");
        String rob = in.next();
        while (!rob.equalsIgnoreCase("true") && !rob.equalsIgnoreCase("false")) {
            System.out.println("Try again.");
            rob = in.next();
        }
        robust_test = Boolean.parseBoolean(rob);

        if (robust_test) {
            if (!d) {
                System.out.println("Enter the Number of Trials (1000 Preferable): ");
                trialnum = in.nextInt();
                System.out.println("Enter # of Evolutionary Trials (1000+ Accurate): ");
                evol = in.nextInt();
            }
            System.out.println("Enter 0 for J Statistic, 1 for Error: ");
            selection = in.nextInt();
            System.out.println("Robustness Testing: There are " + trialnum + " total trials with " + evol +
                    " evolutions on each trial.");
        }
        if (!robust_test) // if not doing robustness testing
        {
            if (!d) {
                System.out.println("Direct Cell Kill?");
                direct = in.nextBoolean();
                System.out.println("Indirect Cell Kill?");
                indirect = in.nextBoolean();
            }
            System.out.println("Enter the Number of Patients: ");
            num_patients = in.nextInt(); // Enter the number of patients tested
        }
        if (direct && !indirect)
            System.out.print("Direct = True, Indirect = False");
        if (!direct && indirect)
            System.out.print("Direct = False, Indirect = True");
        if (direct && indirect)
            System.out.print("Direct = True, Indirect = True");
        if (!direct && !indirect)
            System.out.print("Direct = False, Indirect = False");
        System.out.println("\nNumber of Patients: " + num_patients);
        System.out.println("\nStarting...");

        if (robust_test) {
            String filename = "patientdata.csv"; // using sample patient data for comparison
            // Conducts robustness testing based on generated patients from experimental data
            Robust.Robust_Patient(filename, allpts, datastore, hour, selection, trialnum, evol, direct, indirect, randint);
            writeToOutputFile("Lambda,Sensitivity,Specificity,J Value,Error", "data.csv");
            for (Cohort a : datastore)
                writeToOutputFile(a.getlambda() + "," + a.getSensitivity() + "," + a.getSpecificity() + "," +
                        a.getMaxtruetotal() + "," + a.geterror(), "data.csv");
        } else {
            boolean pretreat = false; // signifies whether an early growth period is used

            // double lambda = 0.1; // defines an arbitrary growth rate for all patients
            // Creates random patients with pre-defined parameters without filtering
            // allpts = In_Silico.PatientSpawner(num_patients, hour, lambda, direct, indirect, pretreat);

            // Conducts random parameter selection in pre-defined experimental-derived ranges
            // allpts = Dose.cumul_dose(allpts, direct, indirect, pretreat, num_patients, hour, randint);

            // Conducts a grid search to simulate patients based on parameter ranges defined in function
            Dose.Grid_Search(allpts, direct, indirect, pretreat, hour, randint);
        }

        // Save all patient values to "All_Values.csv in working directory"
        writeToOutputFile("Lambda,Alpha,Delta,PSI,Dose,Size", "All_Values.csv");
        for (Patient b : allpts)
            writeToOutputFile(b.getlambda() + "," + b.getalpha() + "," + b.getdelta() + "," + b.getPSI() + ","
                    + b.getMinDose() + "," + b.getFractionSize(), "All_Values.csv");
    }
}