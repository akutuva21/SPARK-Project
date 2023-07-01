public class ModelIntroduction {
    public static void main(String[] args) throws Exception {
        
        Boolean lrc_filter, dose_filter, time_filter, logistic_start, direct, indirect;
        double[] alpha_range, delta_range, lambda_range, frac_range, psi_range;
        double alpha_incr, delta_incr, lambda_incr, frac_incr, psi_incr;
        String values_name, volume_name, k_name;

        // Figure 2

        // Panel A: Logistic Growth Model

        lrc_filter = false;
        dose_filter = false;
        time_filter = false;
        direct = false; indirect = false;
        logistic_start = true; // this means that v0 = 0.1
        alpha_range = new double[]{0};
        delta_range = new double[]{0};
        lambda_range = new double[]{0.5}; // growth rate set constant at 0.5
        frac_range = new double[]{2}; // this is irrelevant since DVR/CCR not applied
        psi_range = new double[]{0.1/100}; // this allows the carrying capacity to hit 100
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

        // Panel B: DVR Model Sample

        lrc_filter = false; // not checking for LRC
        dose_filter = false; // not checking for dose constraints
        time_filter = false; // not checking for time constraints
        direct = true; indirect = false;
        logistic_start = false;
        alpha_range = new double[]{0.12}; // alpha death rate set constant, can be modified
        delta_range = new double[]{0};
        lambda_range = new double[]{0.1}; // growth rate set constant, can be modified
        frac_range = new double[]{2}; // fraction size set constant, can be modified
        psi_range = new double[]{0.8}; // PSI set constant, can be modified
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

        // Panel B: CCR Model Sample

        lrc_filter = false; // not checking for LRC
        dose_filter = false; // not checking for dose constraints
        time_filter = false; // not checking for time constraints
        direct = false; indirect = true;
        logistic_start = false;
        alpha_range = new double[]{0};
        delta_range = new double[]{0.1}; // delta death rate set constant, can be modified
        lambda_range = new double[]{0.1}; // growth rate set constant, can be modified
        frac_range = new double[]{2}; // fraction size set constant, can be modified
        psi_range = new double[]{0.8}; // PSI set constant, can be modified
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