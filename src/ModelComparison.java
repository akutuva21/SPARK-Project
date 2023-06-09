import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ModelComparison {

    /**
     * Used for rounding a double value to a given place
     * @param value Any double value
     * @param place integer to indicate how many decimal places to round to
     * @return String representation of rounded double value
     */
    public static String round(double value, double place) {
        double rounded = Math.round(value * Math.pow(10, place)) / Math.pow(10, place);
        return String.valueOf(rounded);
    }

    /**
     * Used for writing a String str to a given file in a csv format (must include ',')
     * @param str String to be written to file
     * @param filename name of file to write to
     * @param place integer to indicate how many decimal places to round to
     * @throws Exception
     */
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

    /**
     * Used for converting a list of patients into a csv format to be written to a file
     * @param s StringBuilder object
     * @param filename name of file to write to
     * @param allpts ArrayList (vector) of patient objects
     * @param direct boolean for DVR (Direct Volume Reduction)
     * @param indirect boolean for CCR (Indirect Carrying Capacity Reduction)
     * @param option integer to indicate what type of data is being written (1 = Volume, 2 = K, 3 = PSI, 4 = dV) 
     * @throws Exception
     */
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

        /* DVR or CCR enabled */
        boolean direct; // boolean for DVR (Direct Volume Reduction)
        boolean indirect; // boolean for CCR (Indirect Carrying Capacity Reduction)

        /* modes of testing */
        boolean lrc_filter; // Whether to end if LRC is achieved (currently set at 32.2% decrease in tumor volume)
        boolean dose_filter; // Whether to cap the number of doses at a maximum dosage
        boolean time_filter; // Whether to cap the time at a maximum number of days/weeks of treatment
        boolean logistic_start; // Whether to keep v0 at 100 (false - default) or start at 0.1 (true - logistic growth)

        /* ranges of parameters to be tested */
        double[] alpha_range; // range of alpha values to be tested, if one value provided then only that value is tested
        double[] delta_range; // range of delta values to be tested, if one value provided then only that value is tested
        double[] lambda_range; // range of lambda values to be tested, if one value provided then only that value is tested
        double[] psi_range; // range of psi values to be tested, if one value provided then only that value is tested
        double[] frac_range; // range of fractions to be tested, if one value provided then only that value is tested

        /* names of the files containing the data */
        String volume_name; // name of the file containing the volume data
        String k_name; // name of the file containing the carrying capacity data
        String values_name; // name of the file containing the values of the parameters for each in-silico patient and the corresponding dose
        
        /* increments of the parameters to be tested */
        double alpha_incr; // increment of the alpha values to be tested, given alpha_range contains 2 values
        double delta_incr; // increment of the delta values to be tested, given delta_range contains 2 values
        double lambda_incr; // increment of the lambda values to be tested, given lambda_range contains 2 values
        double psi_incr; // increment of the psi values to be tested, given psi_range contains 2 values
        double frac_incr; // increment of the fractionation values to be tested, given frac_range contains 2 values
    }

    /**
     * Main method for running the model comparison
     * @param direct boolean for DVR (Direct Volume Reduction)
     * @param indirect boolean for CCR (Indirect Carrying Capacity Reduction)
     * @param lrc_filter boolean for whether to end if LRC is achieved (currently set at 32.2% decrease in tumor volume)
     * @param dose_filter boolean for whether to cap the number of doses at a maximum dosage
     * @param time_filter boolean for whether to cap the time at a maximum number of days/weeks of treatment
     * @param logistic_start boolean for whether to keep v0 at 100 (false - default) or start at 0.1 (true - logistic growth)
     * @param alpha_range range of alpha values to be tested, if one value provided then only that value is tested
     * @param delta_range range of delta values to be tested, if one value provided then only that value is tested
     * @param lambda_range range of lambda values to be tested, if one value provided then only that value is tested
     * @param psi_range range of psi values to be tested, if one value provided then only that value is tested
     * @param frac_range range of fractions to be tested, if one value provided then only that value is tested
     * @param alpha_incr increment of the alpha values to be tested, given alpha_range contains 2 values
     * @param delta_incr increment of the delta values to be tested, given delta_range contains 2 values
     * @param lambda_incr increment of the lambda values to be tested, given lambda_range contains 2 values
     * @param psi_incr increment of the psi values to be tested, given psi_range contains 2 values
     * @param frac_incr increment of the fractions to be tested, given frac_range contains 2 values
     * @param values_name name of the file containing the values of the parameters for each in-silico patient and the corresponding dose
     * @param volume_name name of the file containing the volume data
     * @param k_name name of the file containing the carrying capacity data
     * @throws Exception
     */
    public static void extracted(boolean direct, boolean indirect, boolean lrc_filter, boolean dose_filter, 
            boolean time_filter, boolean logistic_start, double[] alpha_range, double[] delta_range, double[] lambda_range, 
            double[] psi_range, double[] frac_range, double alpha_incr, double delta_incr, double lambda_incr, 
            double psi_incr, double frac_incr, String values_name, String volume_name, String k_name) throws Exception {

        int place = 5; // number of decimal places to round to

        /* Signify times of treatment */
        ArrayList<Double> hour = new ArrayList<>(); // hour(s) of treatment (current set at 6 am only - can be modified for hyperfractionation)
        hour.add(6.0); // 6 AM treatment

        /* pretreatment = whether treatment prior to patient growth is simulated */
        boolean pretreat = false; // signifies whether an early growth period is used
        boolean psi_check = true; // used in in-silico testing to see whether V ever exceeds K (DVR + CCR Case)
        
        boolean include_psi = false; // Whether PSI values are stored (if modified over time)
        boolean include_dv = false; // Whether changes are volume are stored over time (if modified over time)

        ArrayList<Patient> allpts = new ArrayList<>(); // creates a blank arraylist (vector) of patient objects

        double[] incr_range = new double[]{0, 0, 0, 0, 0}; // stores the increments of each parameter
        
        if (direct) {
            // if alpha_range contains 2 values, then alpha_incr is used as the increment
            incr_range[0] = alpha_range.length > 1 ? alpha_incr : 0;
        }
        if (indirect) {
            // if delta_range contains 2 values, then delta_incr is used as the increment
            incr_range[1] = delta_range.length > 1 ? delta_incr : 0;
        }
        
        // if lambda_range contains 2 values, then lambda_incr is used as the increment
        incr_range[2] = lambda_range.length > 1 ? lambda_incr : 0;

        // if psi_range contains 2 values, then psi_incr is used as the increment
        incr_range[3] = psi_range.length > 1 ? psi_incr : 0;

        // if frac_range contains 2 values, then frac_incr is used as the increment
        incr_range[4] = frac_range.length > 1 ? frac_incr : 0;

        boolean include_values = values_name != null && !values_name.isEmpty(); // if values_name is not empty, include_values is true
        boolean include_volume = volume_name != null && !volume_name.isEmpty(); // if volume_name is not empty, include_volume is true
        boolean include_k = k_name != null && !k_name.isEmpty(); // if k_name is not empty, include_k is true
        
        Dose.gridSearch(allpts, hour, direct, indirect, pretreat, psi_check, include_k, include_psi, include_dv, lrc_filter, dose_filter, time_filter, logistic_start, lambda_range, alpha_range, delta_range, psi_range, frac_range, incr_range);

        // if values_name is not empty, write the values of the parameters for each in-silico patient and the corresponding dose to the file
        if (include_values)
        {
            String extension = ".csv";
            writeToOutputFile("Lambda,Alpha,Delta,PSI,Dose,Size", values_name + extension, place);

            for (Patient b : allpts)
                writeToOutputFile(b.getlambda() + "," + b.getalpha() + "," +
                        b.getdelta() + "," + b.getPSI() + ","
                        + b.getMinDose() + "," + b.getFractionSize(), values_name + extension, place);
        }

        // if volume_name is not empty, write the volume data to the file
        if (include_volume) {
            StringBuilder s = new StringBuilder();
            processAndWriteData(s, volume_name + ".csv", allpts, direct, indirect, 1);
        }
        
        // if k_name is not empty, write the carrying capacity data to the file
        if (include_k) {
            StringBuilder s = new StringBuilder();
            processAndWriteData(s, k_name + ".csv", allpts, direct, indirect, 2);
        }
    }
}