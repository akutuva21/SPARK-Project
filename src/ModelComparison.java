import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
// import java.util.Scanner;

public class ModelComparison {

    public static String round(double value, double place) {
        double rounded = Math.round(value * Math.pow(10, place)) / Math.pow(10, place);
        return String.valueOf(rounded);
    }

    // Used for writing a String str to a given file in a csv format (must include ',')
    public static void writeToOutputFile(String str, String filename, int place) throws Exception {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)))) {
            String[] values = str.split("(?<!\\\\),");
            DecimalFormat decimalFormat = new DecimalFormat("#." + "#".repeat(Math.max(0, place)));

            for (int i = 0; i < values.length; i++) {
                String value = values[i].trim(); // Remove leading/trailing whitespaces

                if (value.matches("\\d+(\\.\\d+)?")) {
                    value = decimalFormat.format(Double.parseDouble(value));
                }
                pw.print(value);

                if (i < values.length - 1) {
                    pw.print(",");
                }
            }
            pw.println();
        }
    }

    public static void processAndWriteData(StringBuilder s, String filename, List<Patient> allpts, boolean direct, boolean indirect, int option) throws Exception {
        s.append("Time").append(",");
        for (double d : allpts.get(0).getData().get(0))
        {
            s.append(d).append(',');
        }
        if (!s.isEmpty())
            s.deleteCharAt(s.length() - 1);
        int place = 5;
        writeToOutputFile(s.substring(0, s.length() - 1), filename, place);

        for (Patient p : allpts) {
            s.setLength(0);
            if (direct & indirect)
                s.append("Lambda = ").append(round(p.getlambda(), place)).append(" & Alpha = ").append(round(p.getalpha(), place)).append(" & Delta = ").append(round(p.getdelta(), place)).append(",");
            else if (direct)
                s.append("Lambda = ").append(round(p.getlambda(), place)).append(" & Alpha = ").append(round(p.getalpha(), place)).append(",");
            else if (indirect)
                s.append("Lambda = ").append(round(p.getlambda(), place)).append(" & Delta = ").append(round(p.getdelta(), place)).append(",");
            else
                s.append("Lambda = ").append(round(p.getlambda(), place)).append(",");
            for (double d : p.getData().get(option))
            {
                s.append(d).append(',');
            }
            if (!s.isEmpty())
                s.deleteCharAt(s.length() - 1);
            writeToOutputFile(s.substring(0, s.length() - 1), filename, place);
        }
    }

    public static void main(String[] args) throws Exception {
        int place = 5;

        /* Signify times of treatment */
        ArrayList<Double> hour = new ArrayList<>(); // hour of treatment (current set at 6 am)
        hour.add(6.0); // 6 AM treatment

        /* pretreatment = whether treatment prior to patient growth is simulated */
        boolean pretreat = false; // signifies whether an early growth period is used
        boolean psi_check = true; // used in in-silico testing to see whether V ever exceeds K (Direct + Indirect case)

        /* direct and/or indirect cell kill enabled */
        boolean direct; // boolean for direct cell kill
        boolean indirect; // boolean for indirect cell kill

        boolean include_psi = false; // Whether PSI values are stored (if modified over time)
        boolean include_dv = false; // Whether changes are volume are stored over time (if modified over time)

        /* modes of testing */
        boolean lrc_filter; // Whether to include the point if LRC is achieved
        boolean dose_filter; // Whether to cap the number of doses at a maximum dosage
        boolean time_filter; // Whether to cap the time at a maximum number of days/weeks of treatment
        boolean logistic_start; // Whether to keep v0 at 100 or start at 0.1

        double[] alpha_range; double[] delta_range; double[] lambda_range; double[] psi_range; double[] frac_range;

        String volume_name, k_name, values_name;
        double alpha_incr; double delta_incr; double lambda_incr; double psi_incr; double frac_incr;

        // Figure 1

        // Left Panel
        lrc_filter = false;
        dose_filter = false;
        time_filter = false;
        direct = false; indirect = false;
        logistic_start = true;
        alpha_range = new double[]{0};
        delta_range = new double[]{0};
        lambda_range = new double[]{0.5};
        frac_range = new double[]{2};
        psi_range = new double[]{0.1/100};
        alpha_incr = 0;
        delta_incr = 0;
        lambda_incr = 0;
        psi_incr = 0;
        frac_incr = 0;
        values_name = "Logistic All_Values";
        volume_name = "Logistic_Volume";
        k_name = "Logistic_k";

        extracted(place, direct, indirect, lrc_filter, dose_filter, time_filter, logistic_start, hour, pretreat, psi_check, 
        include_psi, include_dv, alpha_range, delta_range, lambda_range, psi_range, frac_range, alpha_incr, delta_incr, 
        lambda_incr, psi_incr, frac_incr, values_name, volume_name, k_name);

        // Right Panel DVR

        lrc_filter = false;
        dose_filter = false;
        time_filter = false;
        direct = true; indirect = false;
        logistic_start = false;
        alpha_range = new double[]{0.12};
        delta_range = new double[]{0};
        lambda_range = new double[]{0.1};
        frac_range = new double[]{2};
        psi_range = new double[]{0.8};
        alpha_incr = 0;
        delta_incr = 0;
        lambda_incr = 0;
        psi_incr = 0;
        frac_incr = 0;
        values_name = "Direct All_Values";
        volume_name = "Direct_Volume";
        k_name = "Direct_k";

        extracted(place, direct, indirect, lrc_filter, dose_filter, time_filter, logistic_start, hour, pretreat, psi_check, 
        include_psi, include_dv, alpha_range, delta_range, lambda_range, psi_range, frac_range, alpha_incr, delta_incr, 
        lambda_incr, psi_incr, frac_incr, values_name, volume_name, k_name);

        // Right Panel CCR

        lrc_filter = false;
        dose_filter = false;
        time_filter = false;
        direct = false; indirect = true;
        logistic_start = false;
        alpha_range = new double[]{0};
        delta_range = new double[]{0.1};
        lambda_range = new double[]{0.1};
        frac_range = new double[]{2};
        psi_range = new double[]{0.8};
        alpha_incr = 0;
        delta_incr = 0;
        lambda_incr = 0;
        psi_incr = 0;
        frac_incr = 0;
        values_name = "Indirect All_Values";
        volume_name = "Indirect_Volume";
        k_name = "Indirect_k";

        extracted(place, direct, indirect, lrc_filter, dose_filter, time_filter, logistic_start, hour, pretreat, psi_check, 
        include_psi, include_dv, alpha_range, delta_range, lambda_range, psi_range, frac_range, alpha_incr, delta_incr, 
        lambda_incr, psi_incr, frac_incr, values_name, volume_name, k_name);

        // Figure 2

        // Left Panel

        lrc_filter = false;
        dose_filter = false;
        time_filter = false;
        direct = true; indirect = false;
        logistic_start = false;
        alpha_range = new double[]{0, 0.2};
        delta_range = new double[]{0.1};
        lambda_range = new double[]{0.1};
        frac_range = new double[]{2};
        psi_range = new double[]{0.9};
        alpha_incr = 0.02;
        delta_incr = 0;
        lambda_incr = 0;
        psi_incr = 0;
        frac_incr = 0;
        values_name = "";
        volume_name = "l=0.1,psi=0.9,a";
        k_name = "";

        extracted(place, direct, indirect, lrc_filter, dose_filter, time_filter, logistic_start, hour, pretreat, psi_check, 
        include_psi, include_dv, alpha_range, delta_range, lambda_range, psi_range, frac_range, alpha_incr, delta_incr, 
        lambda_incr, psi_incr, frac_incr, values_name, volume_name, k_name);

        // Right Panel

        lrc_filter = false;
        dose_filter = false;
        time_filter = false;
        direct = false; indirect = true;
        logistic_start = false;
        alpha_range = new double[]{0};
        delta_range = new double[]{0, 0.2};
        lambda_range = new double[]{0.1};
        frac_range = new double[]{2};
        psi_range = new double[]{0.9};
        alpha_incr = 0;
        delta_incr = 0.02;
        lambda_incr = 0;
        psi_incr = 0;
        frac_incr = 0;
        values_name = "";
        volume_name = "l=0.1,psi=0.9,d";
        k_name = "";

        extracted(place, direct, indirect, lrc_filter, dose_filter, time_filter, logistic_start, hour, pretreat, psi_check, 
        include_psi, include_dv, alpha_range, delta_range, lambda_range, psi_range, frac_range, alpha_incr, delta_incr, 
        lambda_incr, psi_incr, frac_incr, values_name, volume_name, k_name);

        // Figure 3

        // Left Panel

        lrc_filter = false;
        dose_filter = false;
        time_filter = false;
        direct = true; indirect = false;
        logistic_start = false;
        alpha_range = new double[]{0.1};
        delta_range = new double[]{0};
        lambda_range = new double[]{0, 0.1};
        frac_range = new double[]{2};
        psi_range = new double[]{0.7};
        alpha_incr = 0;
        delta_incr = 0;
        lambda_incr = 0.01;
        psi_incr = 0;
        frac_incr = 0;
        values_name = "";
        volume_name = "lam_sweep_direct_psi_0.7";
        k_name = volume_name + "_k";

        extracted(place, direct, indirect, lrc_filter, dose_filter, time_filter, logistic_start, hour, pretreat, psi_check, 
        include_psi, include_dv, alpha_range, delta_range, lambda_range, psi_range, frac_range, alpha_incr, delta_incr, 
        lambda_incr, psi_incr, frac_incr, values_name, volume_name, k_name);

        // Right Panel

        lrc_filter = false;
        dose_filter = false;
        time_filter = false;
        direct = false; indirect = true;
        logistic_start = false;
        alpha_range = new double[]{0};
        delta_range = new double[]{0.1};
        lambda_range = new double[]{0, 0.1};
        frac_range = new double[]{2};
        psi_range = new double[]{0.7};
        alpha_incr = 0;
        delta_incr = 0;
        lambda_incr = 0.01;
        psi_incr = 0;
        frac_incr = 0;
        values_name = "";
        volume_name = "lam_sweep_indirect_psi_0.7";
        k_name = volume_name + "_k";

        extracted(place, direct, indirect, lrc_filter, dose_filter, time_filter, logistic_start, hour, pretreat, psi_check, 
        include_psi, include_dv, alpha_range, delta_range, lambda_range, psi_range, frac_range, alpha_incr, delta_incr, 
        lambda_incr, psi_incr, frac_incr, values_name, volume_name, k_name);

        // Figure 4

        // Left Panel

        lrc_filter = true;
        dose_filter = true;
        time_filter = true;
        direct = true; indirect = false;
        logistic_start = false;
        alpha_range = new double[]{0.06, 0.14};
        delta_range = new double[]{0};
        lambda_range = new double[]{0.07};
        frac_range = new double[]{2};
        psi_range = new double[]{0.6, 1};
        alpha_incr = 0.0005;
        delta_incr = 0;
        lambda_incr = 0;
        psi_incr = 0.0005;
        frac_incr = 0;
        values_name = "Alpha_PSI_dose";
        volume_name = "";
        k_name = "";

        extracted(place, direct, indirect, lrc_filter, dose_filter, time_filter, logistic_start, hour, pretreat, psi_check, 
        include_psi, include_dv, alpha_range, delta_range, lambda_range, psi_range, frac_range, alpha_incr, delta_incr, 
        lambda_incr, psi_incr, frac_incr, values_name, volume_name, k_name);

        // Right Panel

        lrc_filter = true;
        dose_filter = true;
        time_filter = false;
        direct = true; indirect = false;
        logistic_start = false;
        delta_range = new double[]{0};
        frac_range = new double[]{2};
        alpha_incr = 0;
        delta_incr = 0;
        lambda_incr = 0;
        psi_incr = 0;
        frac_incr = 0;
        values_name = "";
        k_name = "";
        lambda_range = new double[]{0.07};

        // top left, top right, bottom right, bottom left
        double[][] alpha_ranges = { {0.08}, {0.12}, {0.12}, {0.08} };
        double[][] psi_ranges_direct = { {0.9}, {0.9}, {0.7}, {0.7} };
        String[] volume_names_direct = {"Volume_Direct_topleft", "Volume_Direct_topright", "Volume_Direct_bottomright", "Volume_Direct_bottomleft"}; 

        for (int i = 0; i < 4; i++)
        {
            extracted(place, direct, indirect, lrc_filter, dose_filter, time_filter, logistic_start, hour, pretreat, psi_check, 
            include_psi, include_dv, alpha_ranges[i], delta_range, lambda_range, psi_ranges_direct[i], frac_range, alpha_incr, delta_incr, 
            lambda_incr, psi_incr, frac_incr, values_name, volume_names_direct[i], k_name);
        }

        // Figure 5
        
        // Left Panel

        lrc_filter = true;
        dose_filter = true;
        time_filter = true;
        direct = false; indirect = true;
        logistic_start = false;
        alpha_range = new double[]{0};
        delta_range = new double[]{0.01, 0.09};
        lambda_range = new double[]{0.07};
        frac_range = new double[]{2};
        psi_range = new double[]{0.6, 1};
        alpha_incr = 0;
        delta_incr = 0.001;
        lambda_incr = 0;
        psi_incr = 0.001;
        frac_incr = 0;
        values_name = "Delta_PSI_dose";
        volume_name = "";
        k_name = "";

        extracted(place, direct, indirect, lrc_filter, dose_filter, time_filter, logistic_start, hour, pretreat, psi_check, 
        include_psi, include_dv, alpha_range, delta_range, lambda_range, psi_range, frac_range, alpha_incr, delta_incr, 
        lambda_incr, psi_incr, frac_incr, values_name, volume_name, k_name);

        // Right Panel

        lrc_filter = true;
        dose_filter = true;
        time_filter = true;
        direct = false; indirect = true;
        logistic_start = false;
        alpha_range = new double[]{0};
        frac_range = new double[]{2};
        alpha_incr = 0;
        delta_incr = 0;
        lambda_incr = 0;
        psi_incr = 0;
        frac_incr = 0;
        values_name = "";
        k_name = "";
        lambda_range = new double[]{0.07};

        // top left, top right, bottom right, bottom left
        double[][] delta_ranges = { {0.03}, {0.07}, {0.07}, {0.03} };
        double[][] psi_ranges_indirect = { {0.9}, {0.9}, {0.7}, {0.7} };
        String[] volume_names_indirect = {"Volume_Indirect_topleft", "Volume_Indirect_topright", "Volume_Indirect_bottomright", "Volume_Indirect_bottomleft"};
        String[] k_names = {"k_Indirect_topleft", "k_Indirect_topright", "k_Indirect_bottomright", "k_Indirect_bottomleft"};

        for (int i = 0; i < 4; i++)
        {
            extracted(place, direct, indirect, lrc_filter, dose_filter, time_filter, logistic_start, hour, pretreat, psi_check, 
            include_psi, include_dv, alpha_range, delta_ranges[i], lambda_range, psi_ranges_indirect[i], frac_range, alpha_incr, 
            delta_incr, lambda_incr, psi_incr, frac_incr, values_name, volume_names_indirect[i], k_names[i]);
        }

    }

    private static void extracted(int place, boolean direct, boolean indirect, boolean lrc_filter, boolean dose_filter, 
            boolean time_filter, boolean logistic_start, ArrayList<Double> hour, boolean pretreat, boolean psi_check, 
            boolean include_psi, boolean include_dv, double[] alpha_range, double[] delta_range, double[] lambda_range, 
            double[] psi_range, double[] frac_range, double alpha_incr, double delta_incr, double lambda_incr, 
            double psi_incr, double frac_incr, String values_name, String volume_name, String k_name) throws Exception {

        ArrayList<Patient> allpts = new ArrayList<>(); // creates a blank arraylist (vector) of patient objects

        double[] incr_range = new double[]{0, 0, 0, 0, 0};
        if (direct) {
            incr_range[0] = alpha_range.length > 1 ? alpha_incr : 0;
        }
        if (indirect) {
            incr_range[1] = delta_range.length > 1 ? delta_incr : 0;
        }
        incr_range[2] = lambda_range.length > 1 ? lambda_incr : 0;

        incr_range[3] = psi_range.length > 1 ? psi_incr : 0;

        incr_range[4] = frac_range.length > 1 ? frac_incr : 0;

        boolean include_values = values_name != null && !values_name.isEmpty(); // if values_name is not empty, include_values is true
        boolean include_volume = volume_name != null && !volume_name.isEmpty(); // if volume_name is not empty, include_volume is true
        boolean include_k = k_name != null && !k_name.isEmpty(); // if k_name is not empty, include_k is true
        
        Dose.gridSearch(allpts, hour, direct, indirect, pretreat, psi_check, include_k, include_psi, include_dv, lrc_filter, dose_filter, time_filter, logistic_start, lambda_range, alpha_range, delta_range, psi_range, frac_range, incr_range);

        if (include_values) 
        {
            String extension = ".csv";
            writeToOutputFile("Lambda,Alpha,Delta,PSI,Dose,Size", values_name + extension, place);

            for (Patient b : allpts)
                writeToOutputFile(b.getlambda() + "," + b.getalpha() + "," +
                        b.getdelta() + "," + b.getPSI() + ","
                        + b.getMinDose() + "," + b.getFractionSize(), values_name + extension, place);
        }

        if (include_volume) {
            StringBuilder s = new StringBuilder();
            processAndWriteData(s, volume_name + ".csv", allpts, direct, indirect, 1);
        }
        if (include_k) {
            StringBuilder s = new StringBuilder();
            processAndWriteData(s, k_name + ".csv", allpts, direct, indirect, 2);
        }
    }
}