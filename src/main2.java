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
        ArrayList<Cohort> datastore = new ArrayList<>(); //stores lambda/alpha/J statistic
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
            System.out.println("Robustness Testing: There are " + trialnum + " total trials with " + evol + " evolutions on each trial.");
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
            String filename = "patientdata.csv";
            Robust.Robust_Patient(filename, allpts, datastore, hour, selection, trialnum, evol, direct, indirect, randint);
            writeToOutputFile("Lambda,Sensitivity,Specificity,J Value,Error", "data.csv");
            for (Cohort a : datastore)
                writeToOutputFile(a.getlambda() + "," + a.getSensitivity() + "," + a.getSpecificity() + "," + a.getMaxtruetotal() + "," + a.geterror(), "data.csv");
        } else {
            double lambda = 0.1;
            boolean pretreat = false;
            //allpts = In_Silico.PatientSpawner(num_patients, hour, lambda, direct, indirect, pretreat);
            //allpts = Dose.cumul_dose(allpts, direct, indirect, pretreat, num_patients, hour, randint);
            Dose.Grid_Search(allpts, direct, indirect, pretreat, hour, randint);
        }

        writeToOutputFile("Lambda,Alpha,Delta,PSI,Dose,Size", "All_Values.csv"); // Save all patient values to "All_Values.csv"
        for (Patient b : allpts)
            writeToOutputFile(b.getlambda() + "," + b.getalpha() + "," + b.getdelta() + "," + b.getpsi() + "," + b.getmin_dose() + "," + b.getfraction_size(), "All_Values.csv");

//        StringBuilder s = new StringBuilder();
//        s.append("Time").append(",");
//        ArrayList<Double> maxtime = allpts.get(0).getdata().get(0);
//        int scale = (int) Math.pow(10, 2);
//        for (Patient p : allpts)
//        {
//            if (direct)
//            {
//                p.setalpha((double) Math.round(p.getalpha() * scale) / scale);
//                s.append("Lambda = ").append(p.getlambda()).append(" & Alpha = ").append(p.getalpha()).append(",");
//            }
//            else if (indirect)
//            {
//                p.setdelta((double) Math.round(p.getdelta() * scale) / scale);
//                s.append("Lambda = ").append(p.getlambda()).append(" & Delta = ").append(p.getdelta()).append(",");
//            }
//            else
//                s.append("Lambda = ").append(p.getlambda()).append(",");
//            if (p.getdata().get(0).size() > maxtime.size())
//                maxtime = p.getdata().get(0);
//        }
//        writeToOutputFile(s.substring(0, s.length() - 1),"Volume_Data.csv"); // Save all time point values to "Model_Comparison.csv"
//
//        for (int k = 0; k < maxtime.size(); k++) {
//            s = new StringBuilder();
//            s.append(maxtime.get(k)).append(",");
//            for (Patient b : allpts)
//                if (k < b.getdata().get(1).size())
//                    s.append(b.getdata().get(1).get(k)).append(",");
//                else
//                    s.append(",");
//            writeToOutputFile(s.substring(0, s.length() - 1),"Volume_Data.csv"); // Same as above (uncomment both to work)
//        }
//
//        s = new StringBuilder();
//        for (Patient p : allpts)
//        {
//            s.append(p.get_v0()).append(",");
//        }
//        //writeToOutputFile(s.substring(0, s.length() - 1),"v0_Distribution.csv"); // v0 distribution
//
//        s = new StringBuilder();
//        for (Patient p : allpts)
//        {
//            s.append(p.get_k()).append(",");
//        }
//        //writeToOutputFile(s.substring(0, s.length() - 1),"K_Distribution.csv"); // k distribution
//
//
//        if (direct)
//        {
//            s = new StringBuilder();
//            for (Patient p : allpts)
//            {
//                s.append(p.getalpha()).append(",");
//            }
//            //writeToOutputFile(s.substring(0, s.length() - 1),"Alpha_Distribution.csv"); // alpha distribution
//        }
//
//        if (indirect)
//        {
//            s = new StringBuilder();
//            for (Patient p : allpts)
//            {
//                s.append(p.getdelta()).append(",");
//            }
//            //writeToOutputFile(s.substring(0, s.length() - 1),"Delta_Distribution.csv"); // delta distribution
//        }
//
//
//        s = new StringBuilder();
//        s.append("Time").append(",");
//        for (Patient allpt : allpts)
//            s.append("Lambda = ").append(allpt.getlambda()).append(" / Alpha = ").append(allpt.getalpha()).append(" / Delta = ").append(allpt.getdelta()).append(",");
//        writeToOutputFile(s.substring(0, s.length() - 1),"k_vals.csv"); // k distribution
//
//        for (int k = 0; k < allpts.get(0).getdata().get(0).size(); k++) {
//            s = new StringBuilder();
//            s.append(allpts.get(0).getdata().get(0).get(k)).append(",");
//            for (Patient b : allpts)
//                s.append(b.getdata().get(2).get(k)).append(",");
//            writeToOutputFile(s.substring(0, s.length() - 1),"k_vals.csv"); // k values over time for each patient
//        }
//        /*
//        s = new StringBuilder();
//        s.append("dV").append(",");
//        for (int i = 0; i < allpts.size(); i++)
//            s.append("Patient ").append(i + 1).append(",");
//        //writeToOutputFile(s.substring(0, s.length() - 1),"dv_vals.csv");
//        for (int k = 0; k < allpts.get(0).getdata().get(0).size() - 1; k++) {
//            s = new StringBuilder();
//            s.append(allpts.get(0).getdata().get(0).get(k)).append(",");
//            for (Patient b : allpts)
//                s.append(b.getdata().get(3).get(k)).append(",");
//            //writeToOutputFile(s.substring(0, s.length() - 1),"dv_vals.csv"); // dv values over time for each patient
//        }
//
//        s = new StringBuilder();
//        s.append("PSI").append(",");
//        for (int i = 0; i < allpts.size(); i++)
//            s.append("Patient ").append(i + 1).append(",");
//        //writeToOutputFile(s.substring(0, s.length() - 1),"psi_vals.csv");
//        for (int k = 0; k < allpts.get(0).getdata().get(0).size() - 1; k++) {
//            s = new StringBuilder();
//            s.append(allpts.get(0).getdata().get(0).get(k)).append(",");
//            for (Patient b : allpts)
//                s.append(b.getdata().get(4).get(k)).append(",");
//            //writeToOutputFile(s.substring(0, s.length() - 1),"psi_vals.csv"); // psi values over time for each patient
//        }
//        */
//    }
//}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Old Code

/*
    public static double[] JError(ArrayList<Patient> allpts, double lambda, Random randnum) {
        int TP = 0;
        int FN = 0;
        int TN = 0;
        int FP = 0;
        int total = 0;
        double psi;
        double k;
        double error = 0;

        int randomNum = randnum.nextInt((allpts.size() / 3) + 1);

        for (int j = 0; j < allpts.size() / 3; j++) {
            //if (j == randomNum) // leave one out
                //continue;

            double starttime = allpts.get(j).getdata().get(0).get(0);
            double middletime = allpts.get(j).getdata().get(0).get(1);
            double endtime = allpts.get(j).getdata().get(0).get(2);

            //System.out.println(starttime + " " + middletime + " " + endtime);

            double startvol = allpts.get(j).getdata().get(1).get(0);
            double middlevol = allpts.get(j).getdata().get(1).get(1);
            double endvol = allpts.get(j).getdata().get(1).get(2);

            //System.out.println(startvol + " " + middlevol + " " + endvol);

            double expterm = Math.exp(lambda * allpts.get(j).getmu() * (middletime - starttime));
            psi = Math.pow((Math.pow(startvol, allpts.get(j).getmu()) * expterm - Math.pow(middlevol, allpts.get(j).getmu())) /
                    (Math.pow(middlevol, allpts.get(j).getmu()) * (expterm - 1)), 1.0/allpts.get(j).getmu());
            k = middlevol / psi;

            */
            /* double expterm = Math.exp(lambda * (middletime - starttime));
            k = (middlevol * startvol * (expterm - 1)) / (startvol * expterm - middlevol);
            psi = middlevol / k;*//*


            if (isNaN(psi) || psi <= 0 || psi >= 1)
            {
                total++;
                continue;
            }
            allpts.get(j).setpsi(psi);

            double gamma = 1 - Math.exp(-allpts.get(j).getalpha() * allpts.get(j).getfraction_size() - (allpts.get(j).getalpha() / allpts.get(j).getabratio()) * Math.pow(allpts.get(j).getfraction_size(), 2));

            double A1 = Math.pow((k / startvol), allpts.get(j).getmu()) - 1; //original function
            double start = k / Math.pow((1 + A1 * Math.exp(-lambda * (middletime - starttime))), 1/allpts.get(j).getmu()); // essential - tracks volume at tp1, original function
            double end = start;

            for(double t = middletime; t <= endtime; t += allpts.get(j).getfraction_freq())
            {
                end = k / Math.pow(1 + (Math.pow((k / end), allpts.get(j).getmu()) - 1) * Math.exp(-lambda * (t - middletime)), 1 / allpts.get(j).getmu()); //original function
                k = k * (1 - allpts.get(j).getdelta()); // indirect cell kill
                end = end * gamma * (1 - Math.pow(end/k, allpts.get(j).getmu())); // used in ARO paper
            }

            double predicteddiff = Math.abs((end - start) / start); // predicted change in volume between tps
            double actualdifference = Math.abs((endvol - middlevol)) / (middlevol); //change in volume between tps

            error += Math.pow(end - endvol, 2);

            if (predicteddiff >= 0.322 && actualdifference >= 0.322) {
                TP++;
            }

            if (predicteddiff < 0.322 && actualdifference < 0.322) {
                TN++;
            }

            if (predicteddiff >= 0.322 && actualdifference < 0.322) {
                FP++;
            }

            if (predicteddiff < 0.322 && actualdifference >= 0.322) {
                FN++;
            }
            total++;
        }
        double[] returnarray = new double[2];
        double sensitivity = (double) (TP) / (double) (TP + FN);
        double specificity = (double) (TN) / (double) (TN + FP);
        returnarray[0] = sensitivity + specificity - 1;
        returnarray[1] = Math.sqrt(error / (double) total);
        return returnarray;
    }

    /*StringBuilder s = new StringBuilder();
        for (int k = 0; k < allpts.get(0).getdata().get(0).size(); k++) {
            s.append(allpts.get(0).getdata().get(0).get(k)).append(",");
        }
        s = new StringBuilder(s.substring(0, s.length() - 1));
        writeToOutputFile(s.toString(),"Fake_Patients.csv");

        for (Patient b : allpts)
        {
            s = new StringBuilder();
            for (int k = 0; k < b.getdata().get(0).size(); k++) {
                s.append(b.getdata().get(1).get(k)).append(",");
            }
            s = new StringBuilder(s.substring(0, s.length() - 1));
            writeToOutputFile(s.toString(),"Fake_Patients.csv");
        }

        /*s = new StringBuilder();
        for (int k = 0; k < allpts.get(0).getdata().get(0).size(); k++)
            s.append(allpts.get(0).getdata().get(0).get(k)).append(",");
        writeToOutputFile(s.toString(),"k_vals.csv");
        for (Patient b : allpts)
        {
            s = new StringBuilder();
            for (int k = 0; k < b.getdata().get(2).size(); k++) {
                s.append(b.getdata().get(2).get(k)).append(",");
            }
            s = new StringBuilder(s.substring(0, s.length() - 1));
            writeToOutputFile(s.toString(),"k_vals.csv");
        }
        */
    }
}