import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.lang.Math;
// import java.util.Scanner;

public class main2 {

    public static String round(String d, double place)
    {
        double d_new = Double.parseDouble(d);
        double rounded = (int)(d_new * Math.pow(d_new, place)) / Math.pow(d_new, place);
        System.out.println(rounded);
        return String.valueOf(rounded);
    }

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

    public static void processAndWriteData(StringBuilder s, String filename, List<Patient> allpts, boolean direct, boolean indirect, double scale, int option) throws Exception {
        s.append("Time").append(",");
        for (Patient p : allpts)
        {
            for (double d : p.getData().get(0))
            {
                s.append(d).append(',');
            }
            if (!s.isEmpty())
                s.deleteCharAt(s.length() - 1);
        }
        writeToOutputFile(s.substring(0, s.length() - 1), filename);

        for (Patient p : allpts) {
            s.setLength(0);
            if (direct & indirect)
                s.append("Lambda = ").append(p.getlambda()).append(" & Alpha = ").append(p.getalpha()).append(" & Delta = ").append(p.getdelta()).append(",");
            else if (direct)
                s.append("Lambda = ").append(p.getlambda()).append(" & Alpha = ").append(p.getalpha()).append(",");
            else if (indirect)
                s.append("Lambda = ").append(p.getlambda()).append(" & Delta = ").append(p.getdelta()).append(",");
            else
                s.append("Lambda = ").append(p.getlambda()).append(",");
            for (double d : p.getData().get(option))
            {
                s.append(d).append(',');
            }
            if (!s.isEmpty())
                s.deleteCharAt(s.length() - 1);
            writeToOutputFile(s.substring(0, s.length() - 1), filename);
        }
    }

    static FileWriter fw = null;
    static BufferedWriter bw = null;
    static PrintWriter pw = null;

    public static void main(String[] args) throws Exception {
        final int seed = 7; // random seed value set
        Random r = new Random(seed); // declare a random object with seed

        /* Signify default number of trials / evolutions / patients */
        // boolean d = true; // boolean for default parameters
        int trialnum = 100; // number of trials/sets of cohorts
        int evol = 1000; // number of evolutions (if robustness is used)
        int num_patients = 10000; // default number of in-silico patients to be used

        /* Signify times of treatment */
        ArrayList<Double> hour = new ArrayList<>(); // hour of treatment (current set at 6 am)
        hour.add(6.0); // 6 AM treatment

        int selection = 1; // boolean/int for whether to sort robustness by RMSE/J statistic
        ArrayList<Cohort> datastore = new ArrayList<>(); // stores lambda/alpha/J statistic

        /* pretreatment = whether treatment prior to patient growth is simulated */
        boolean pretreat = false; // signifies whether an early growth period is used
        boolean psi_check = true; // used in in-silico testing to see whether V ever exceeds K (Direct + Indirect case)

        /* direct and/or indirect cell kill enabled */
        boolean direct = !true; // boolean for direct cell kill
        boolean indirect = !true; // boolean for indirect cell kill
        boolean both = true; // adds both data to the same
        if(both)
        {
            direct = true;
            indirect = true;
        }

        boolean include_volume = true; // Whether Volume values are stored as Volume_Data.csv
        boolean include_k = true; // Whether K values are stored (if modified over time)
        boolean include_psi = true; // Whether PSI values are stored (if modified over time)
        boolean include_dv = !true; // Whether changes are volume are stored over time (if modified over time)

        /* modes of testing */
        boolean robust_test = false; // Indicates whether robustness testing is being done
        boolean spawn_random_pts = false; // Creates random patients with pre-defined parameters (no filter except PSI)
        boolean random_selection = false; // Conducts random parameter selection in pre-defined experimental-derived ranges
        boolean grid_search = true; // Conducts a grid search to simulate patients based on parameter ranges defined in function

        ArrayList<Patient> allpts = new ArrayList<>(); // creates a blank arraylist (vector) of patient objects

        /*// Below lines are useful for potential terminal boolean implementation
        Scanner in = new Scanner(System.in);
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
        }*/

        System.out.println("Direct is " + direct + ", Indirect is " + indirect);
        if (spawn_random_pts)
            System.out.println("Number of Patients: " + num_patients);
        System.out.println("Starting...");

        if (robust_test) {
            String filename = "patientdata.csv"; // using sample patient data for comparison
            // Conducts robustness testing based on generated patients from experimental data
            Robust.robustPatient(filename, allpts, datastore, hour, selection, trialnum, evol, direct, indirect, r);
            writeToOutputFile("Lambda,Sensitivity,Specificity,J Value,Error", "data.csv");
            for (Cohort a : datastore)
                writeToOutputFile(a.getlambda() + "," + a.getSensitivity() + "," + a.getSpecificity() + "," +
                        a.getMaxtruetotal() + "," + a.geterror(), "data.csv");
        }
        else if (spawn_random_pts)
        {
            // double lambda = 0.1; // defines an arbitrary growth rate for all patients
            allpts = In_Silico.patientSpawner(num_patients, hour, direct, indirect, pretreat, psi_check, include_k, include_psi, include_dv);
        }
        else if (random_selection)
        {
            Dose.cumulDose(allpts, num_patients, hour, direct, indirect, pretreat, psi_check, r, include_k, include_psi, include_dv);
        }
        else if (grid_search)
        {
            Dose.gridSearch(allpts, hour, direct, indirect, pretreat, psi_check, include_k, include_psi, include_dv);
        }
        // Save all patient values to a .csv file called "All_Values" in working directory
        String filename = "All_Values";
        String extension = ".csv";
        writeToOutputFile("Lambda,Alpha,Delta,PSI,Dose,Size", filename + extension);

        DecimalFormat df_obj = new DecimalFormat("#.#####");
        for (Patient b : allpts)
            writeToOutputFile(df_obj.format(b.getlambda()) + "," + df_obj.format(b.getalpha()) + "," +
                    df_obj.format(b.getdelta()) + "," + df_obj.format(b.getPSI()) + ","
                    + df_obj.format(b.getMinDose()) + "," + df_obj.format(b.getFractionSize()), filename + extension);

        int scale = (int) Math.pow(10, 2);
        if (include_volume) {
            StringBuilder s = new StringBuilder();
            processAndWriteData(s, "Volume_Data.csv", allpts, direct, indirect, scale, 1);
        }
        if (include_k) {
            StringBuilder s = new StringBuilder();
            processAndWriteData(s, "k_vals.csv", allpts, direct, indirect, scale, 2);
        }
        if (include_psi) {
            StringBuilder s = new StringBuilder();
            processAndWriteData(s, "psi_vals.csv", allpts, direct, indirect, scale, 3);
        }
        if (include_dv) {
            StringBuilder s = new StringBuilder();
            processAndWriteData(s, "dv_vals.csv", allpts, direct, indirect, scale, 4);
        }
    }
}