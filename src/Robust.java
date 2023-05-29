import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Comparator;

import static java.lang.Double.isNaN;

public class Robust {

    // Used for reading patient volume data in a given format (see patientdata.csv - patient #, time, volume)
    public static ArrayList<ArrayList<Double>> readData(String file) throws IOException {
        ArrayList<String[]> content = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.add(line.split(","));
            }
        }
        ArrayList<ArrayList<Double>> converted = new ArrayList<>();
        for (String[] val : content) {
            ArrayList<Double> inside = new ArrayList<>();
            for (String b : val) {
                inside.add(Double.parseDouble(b));
            }
            converted.add(inside);
        }
        return converted;
    }

    // Read Patient Data and Spawn Patient Objects - Driver Code
    public static void robustPatient(String filename, ArrayList<Patient> allpts, ArrayList<Cohort> datastore, ArrayList<Double> hour, int selection, int trialnum, int evol, Boolean direct, Boolean indirect, Random randint) throws IOException {
        final ArrayList<ArrayList<Double>> fulldata = readData(filename);
        double abratio = 10; // assumed to be constant alpha / beta ratio
        double fraction_size = 2; // assumed to be constant fraction size
        double cumul_dose = 68.0; // 66-70 as per ARO
        double lambda = 0.07; // assumes a growth rate of 0.07
        for (int i = 0; i < fulldata.size(); i += 3) // read the data file
        {
            double starttime = fulldata.get(i).get(1);
            double middletime = fulldata.get(i + 1).get(1);
            double endtime = fulldata.get(i + 2).get(1);
            double startvol = fulldata.get(i).get(2);
            double middlevol = fulldata.get(i + 1).get(2);
            double endvol = fulldata.get(i + 2).get(2);

            ArrayList<ArrayList<Double>> patientdata = new ArrayList<>();
            patientdata.add(new ArrayList<>(Arrays.asList(starttime, middletime, endtime)));
            patientdata.add(new ArrayList<>(Arrays.asList(startvol, middlevol, endvol)));

            Patient p = new Patient();
            if (direct)
            {
                double alpha = 0.09;
                p.setalpha(alpha); // alpha set constant
            }
            if (indirect)
            {
                double delta = 0.1 * 1.45/4.31;
                p.setdelta(delta); // delta set constant
            }
            p.setabratio(abratio);
            p.setFractionSize(fraction_size);
            p.setCumulDose(cumul_dose);
            p.setMinDose(cumul_dose);
            p.setData(patientdata);
            p.setlambda(lambda);

            double expterm = Math.exp(p.getlambda() * (middletime - starttime));
            double psi = (startvol * expterm - middlevol) / (middlevol * (expterm - 1));
            p.setPSI(psi);
            allpts.add(p);
        }
        lambdaEvol(allpts, datastore, direct, indirect, hour, selection, trialnum, evol, randint);
    }

    // Robustness testing to regenerate patients with a common lambda
    public static void lambdaEvol(ArrayList<Patient> allpts, ArrayList<Cohort> datastore, boolean direct, boolean indirect, ArrayList<Double> hour, int selection, int trialnum, int evol, Random randint) {
        for (int evolution = 0; evolution < evol; evolution++) // for each evolution
        {
            for (int n = 0; n < trialnum; n++) // trialnum = number of sets of cohort parameters (lambda, J Value, RMSE)
            {
                double lambda = randint.nextDouble() * Math.log(2); // assuming ln(2) is an upper bound - ARO
                for (Patient p : allpts)
                    p.setlambda(lambda); // sets all patients with a common lambda
                double[] vals = robustValid(allpts, lambda, hour, randint, direct, indirect);
                datastore.add(new Cohort(lambda, vals[0], vals[1], vals[2]));
            }
            if (selection == 0) // selection parameter determines which sort to be used
            {
                Evolution(datastore, allpts, trialnum, randint, Cohort.TTSort, hour, direct, indirect);
            } else if (selection == 1) {
                Evolution(datastore, allpts, trialnum, randint, Cohort.ErrorSort.reversed(), hour, direct, indirect);
            }

            if (selection == 0 && datastore.get(0).getMaxtruetotal() != 0.0 && datastore.get(0).getMaxtruetotal() == datastore.get((datastore.size() - 1) / 2).getMaxtruetotal()) {
                System.out.println("Number of Evolutions: " + evolution);
                break;
            } else if (selection == 1 && datastore.get(0).geterror() != 0.0 && datastore.get(0).geterror() == datastore.get((datastore.size() - 1) / 2).geterror()) {
                System.out.println("Number of Evolutions: " + evolution);
                break;
            }

            if ((evolution + 1) % 100 == 0 && (evolution + 1) != trialnum)
                System.out.println("Evolution Number: " + (evolution + 1));
        }
    }

    // Used for robustness validation for a given predefined group of patients
    public static double[] robustValid(ArrayList<Patient> allpts, double lambda, ArrayList<Double> hour, Random randnum, boolean direct, boolean indirect) {
        int TP = 0;
        int TN = 0;
        int FP = 0;
        int FN = 0;
        int total = 0;
        double psi;
        double k;
        double error = 0;

        // randomly select a patient to exclude
        Patient randomNum = allpts.get(randnum.nextInt((allpts.size() / allpts.get(0).getData().size()) + 1));

        for (Patient allpt : allpts) {
            if (allpt == randomNum) // leave one out testing for validation testing
                continue;

            double starttime = allpt.getData().get(0).get(0);
            double middletime = allpt.getData().get(0).get(1);
            double endtime = allpt.getData().get(0).get(allpt.getData().get(0).size() - 1);

            double startvol = allpt.getData().get(1).get(0);
            double middlevol = allpt.getData().get(1).get(1);
            double endvol = allpt.getData().get(1).get(allpt.getData().get(1).size() - 1);

            double expterm = Math.exp(lambda * (middletime - starttime));
            psi = (startvol * expterm - middlevol) / (middlevol * (expterm - 1)); // calculate psi based on first two points

            k = startvol / psi; // calculate k based on psi

            double gamma = 1 - Math.exp(-allpt.getalpha() * allpt.getFractionSize() - (allpt.getalpha() / allpt.getabratio()) * Math.pow(allpt.getFractionSize(), 2));

            double A1 = (k / startvol) - 1;
            double start = k / (1 + A1 * Math.exp(-lambda * (middletime - starttime))); // tracks projected volume at second time point
            double end = start;

            double t = middletime; // simulate tumor volume reduction after initial growth period (time point 1 to time point 2)
            int numdoses = 0;
            double delta_t = 1.0 / 24.0; // time step
            while (t < endtime && numdoses < allpt.getCumulDose() / allpt.getFractionSize()) // while time is less than end or maximum number of doses reached
            {
                t += delta_t;
                double weekend = (int) (t / delta_t) % (7 / delta_t);
                if (weekend <= 120)  // if not weekend
                {
                    if (Dose.ArrayCheck(t, delta_t, hour)) // every day at h hour (6 am)
                    {
                        if (direct) {
                            end -= gamma * end * (1 - (end / k)); // direct cell kill
                        }
                        if (indirect) {
                            k *= (1 - allpt.getdelta()); // indirect cell kill
                        }
                        if (direct || indirect) // if either, increment number of doses
                            numdoses++;
                    }
                }
                end = k / (1 + ((k / end) - 1) * Math.exp(-lambda * delta_t)); // simulate growth between time points
            }
            double predicteddiff = Math.abs((end - start) / start); // predicted change in volume between time points 2 and 3
            double actualdifference = Math.abs((endvol - middlevol)) / (middlevol); //change in volume between time points 2 and 3

            error += Math.pow(end - endvol, 2); // calculate error for rmse

            // tp/tn/fp/fn for whether lrc was reached in constraints
            if (predicteddiff >= 0.322 && actualdifference >= 0.322) TP++;
            else if (predicteddiff < 0.322 && actualdifference < 0.322) TN++;
            else if (predicteddiff >= 0.322 && actualdifference < 0.322) FP++;
            else if (predicteddiff < 0.322 && actualdifference >= 0.322) FN++;

            total++;
        }
        double[] returnarray = new double[3];
        double sensitivity = (double) (TP) / (double) (TP + FN); // calculate sensitivity
        double specificity = (double) (TN) / (double) (TN + FP); // calculate specificity
        if (isNaN(specificity))
            specificity = 0;
        if (isNaN(sensitivity))
            sensitivity = 0;
        returnarray[0] = sensitivity; // return sensitivity
        returnarray[1] = specificity; // returns specificity
        returnarray[2] = Math.sqrt(error / (double) total); // return rmse
        return returnarray;
    }

    private static void Evolution(ArrayList<Cohort> datastore, ArrayList<Patient> allpts, int trialnum, Random randnum, Comparator<Cohort> sorter, ArrayList<Double> hour, boolean direct, boolean indirect) {
        int cross = 0;
        int mut = 0;
        if (datastore.size() > trialnum / 2) // split the list of cohort in half (filtered from previous iteration)
            datastore.subList(trialnum / 2, datastore.size()).clear();
        for (int i = 0; i < trialnum / 2; i++) // for half the patients
        {
            double randomselection = randnum.nextDouble() * 2;
            if (((int) randomselection == 0  || mut >= trialnum / 4) && (cross < trialnum / 4)) // crossing over with taking lambda average of two patients
            {
                int random1 = (int) (randnum.nextDouble() * trialnum / 2);
                int random2 = (int) (randnum.nextDouble() * trialnum / 2);
                datastore.add(new Cohort(datastore.get(random1).getlambda() * datastore.get(random2).getlambda() / 2, 0, 0, 0));
                cross++;
            }
            if (((int) randomselection == 1  || cross >= trialnum / 4) && (mut < trialnum / 4)) // random mutation on lambda values on a given patient to generate a new patient
            {
                int p = (int) (randnum.nextDouble() * trialnum / 2);
                double num = randnum.nextDouble() * 2;
                datastore.add(new Cohort(num * datastore.get(p).getlambda(), 0, 0, 0));
                mut++;
            }
        }
        for (Cohort a : datastore) {
            if (a.getSensitivity() == 0 && a.getSpecificity() == 0 && a.geterror() == 0) // for a new patient, find the sensitivity/specificity/error
            {
                double[] vals = robustValid(allpts, a.getlambda(), hour, randnum, direct, indirect);
                a.setSensitivity(vals[0]);
                a.setSpecificity(vals[1]);
                a.seterror(vals[2]);
                a.setmaxtruetotal(a.getSensitivity() + a.getSpecificity() - 1);
            }
        }
        if (sorter == Cohort.TTSort) // sort patients by by J (high to low)
            datastore.sort(Cohort.TTSort.reversed().thenComparing(Cohort::geterror));
        else // sort patients by RMSE (low to high)
            datastore.sort(Cohort.ErrorSort.thenComparing(Cohort::getMaxtruetotal));
    }
}

    /* double t = allpt.getfraction_freq();
    int numdoses = 0;
    int hour = 6;

    while (numdoses <= allpt.getCumulDose() / allpt.getFractionSize())
    {
        t += 1.0/24.0;
        int weekend = (int)(t * 24) % (7 * 24);
        double growth = Math.exp(-lambda * allpt.getmu() * (1.0 / 24.0));

        if (weekend <= 120) {
            if (In_Silico.nearZero(((int)(t * 24) - hour) % 24, Math.pow(10, -3))) // everyday at 6 am
            {
                double new_k = k * (1 - allpt.getdelta()); //indirect cell kill
                double new_end = end - (gamma * end * (1 - Math.pow(end / k, allpt.getmu()))); // used in ARO paper - direct cell kill, added a negative sign in front of gamma
                if (indirect && ! direct)
                {
                    end = new_k / Math.pow(1 + (Math.pow((new_k / end), allpt.getmu()) - 1) * growth, 1 / allpt.getmu()); //original function
                }
                if (!indirect && direct)
                {
                    end = k / Math.pow(1 + (Math.pow((k / new_end), allpt.getmu()) - 1) * growth, 1 / allpt.getmu()); //original function
                }
                if (indirect && direct)
                {
                    end = new_k / Math.pow(1 + (Math.pow((new_k / new_end), allpt.getmu()) - 1) * growth, 1 / allpt.getmu()); //original function
                }
                numdoses++;
            }
        }
        else
        {
            end = k / Math.pow(1 + (Math.pow((k / end), allpt.getmu()) - 1) * growth, 1 / allpt.getmu()); //original function
        }
     }*/