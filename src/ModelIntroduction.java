public class ModelIntroduction {
    public static void main(String[] args) throws Exception {
        
        Boolean lrc_filter, dose_filter, time_filter, logistic_start, direct, indirect;
        double[] alpha_range, delta_range, lambda_range, frac_range, psi_range;
        double alpha_incr, delta_incr, lambda_incr, frac_incr, psi_incr;
        String values_name, volume_name, k_name;

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

        ModelComparison.extracted(direct, indirect, lrc_filter, dose_filter, time_filter, logistic_start, alpha_range,
        delta_range, lambda_range, psi_range, frac_range, alpha_incr, delta_incr, lambda_incr, psi_incr,
        frac_incr, values_name, volume_name, k_name);

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

        ModelComparison.extracted(direct, indirect, lrc_filter, dose_filter, time_filter, logistic_start, alpha_range, 
        delta_range, lambda_range, psi_range, frac_range, alpha_incr, delta_incr, lambda_incr, psi_incr, 
        frac_incr, values_name, volume_name, k_name);

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

        ModelComparison.extracted(direct, indirect, lrc_filter, dose_filter, time_filter, logistic_start, alpha_range, 
        delta_range, lambda_range, psi_range, frac_range, alpha_incr, delta_incr, lambda_incr, psi_incr, 
        frac_incr, values_name, volume_name, k_name);
    }
}