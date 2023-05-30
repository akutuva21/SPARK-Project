# Direct vs indirect effects of radiotherapy: Opposing impact of tumor proliferation saturation on minimum dose for tumor control
This file contains a brief description of each of the code files used to simulate the various conditions required for DVR (Direct Volume Reduction) and CCR (Tumor Carrying Capacity Reduction) for radiotherapy simulations at a localized tumor region. Each of the files have been organized into various folders to aid in quick accessibility. This does not require any specific environments to run besides a basic Java IDE, such as IntelliJ IDEA, and a Python IDE, such as PyCharm or Visual Studio Code.
> If you have any questions, please feel free to contact Achyudhan Kutuva at akutuva@ufl.edu.
### Figure Data
This contains the data generated by the model simulations to develop the figures. This folder contains sub-folders pertaining to each of the individual figures containing the corresponding data.
### Python Files
This contains all the files needed for generating the respective figures. "Sample_Graphing.py" corresponds to Figure 1. "Death_Sweep.py" corresponds to Figure 2. "Lambda_Sweep.py" corresponds to Figure 3. Finally, "Death_PSI_Cumul_Graphs.py" corresponds to Figures 4 and 5. Within this folder, the "Testing" subfolder refers to a collection of various files primarily relegated for internal testing purposes.
### src
This folder contains a collection of various .java files that contain the data generation features. 'Cohort.java' and 'Robust.java' are unused and are used for robustness testing. 'Dose.java' is a file containing a collection of functions related to generating *in-silico* patients with dose restrictions. 'In_Silico.java' contains a collection of files that generate arbitrary *in-silico* patients given specific patient parameters. 'ModelComparison.java' is the primary driver file that contains all the various settings currently available. 'Patient.java' contains the parameters for the Patient object, which contains patient-specific values along with the corresponding fractionation values.