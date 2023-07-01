#!/bin/bash

# Check if at least one argument is provided
if [ $# -eq 0 ]; then
    echo "Error: Please provide at least one argument to execute the commands inside."
    exit 1
fi

# Get the parent directory path
parent_directory="$(dirname "$(pwd)")"

# Specify the path to the desired directory one level outside
python_directory="$parent_directory/Python Files"
src_directory="$parent_directory/src"

if [ -f "$src_directory/ModelComparison.java" ]; then
    javac "$src_directory/ModelComparison.java"
    java -cp . ModelComparison

    # loop through the arguments
    for arg in "$@"
    do
        case $arg in 
            2)
                if [ -f "$src_directory/ModelIntroduction.java" ]; then
                    javac "$src_directory/ModelIntroduction.java"
                    java -cp . ModelIntroduction

                    # Run the Python file
                    if [ -f "$python_directory/Sample_Graphing.py" ]; then
                        python "$python_directory/Sample_Graphing.py"
                        echo "Figure $arg Generated!"
                    elif [ -f "$python_directory/Sample_Graphing.py" ]; then
                        python3 "$python_directory/Sample_Graphing.py"
                        echo "Figure $arg Generated!"
                    else
                        echo "Python file (Sample_Graphing.py) not found."
                    fi

                else
                    echo "Java file (ModelIntroduction.java) not found."
                fi
                ;;
            3)
                if [ -f "$src_directory/DeathParamSweep.java" ]; then
                    javac "$src_directory/DeathParamSweep.java"
                    java -cp . DeathParamSweep

                    # Run the Python file
                    if [ -f "$python_directory/Death_Sweep.py" ]; then
                        python "$python_directory/Death_Sweep.py"
                        echo "Figure $arg Generated!"
                    elif [ -f "$python_directory/Death_Sweep.py" ]; then
                        python3 "$python_directory/Death_Sweep.py"
                        echo "Figure $arg Generated!"
                    else
                        echo "Python file (Death_Sweep.py) not found."
                    fi

                else
                    echo "Java file (DeathParamSweep.java) not found."
                fi
                ;;
            4)
                if [ -f "$src_directory/LambdaSweep.java" ]; then
                    javac "$src_directory/LambdaSweep.java"
                    java -cp . LambdaSweep

                    # Run the Python file
                    if [ -f "$python_directory/Lambda_Sweep.py" ]; then
                        python "$python_directory/Lambda_Sweep.py"
                        echo "Figure $arg Generated!"
                    elif [ -f "$python_directory/Lambda_Sweep.py" ]; then
                        python3 "$python_directory/Lambda_Sweep.py"
                        echo "Figure $arg Generated!"
                    else
                        echo "Python file (Lambda_Sweep.py) not found."
                    fi

                else
                    echo "Java file (LambdaSweep.java) not found."
                fi
                ;;
            5)
                if [ -f "$src_directory/DVR_CCR_Scatter.java" ]; then
                    javac "$src_directory/DVR_CCR_Scatter.java"
                    java -cp . DVR_CCR_Scatter

                    # Run the Python file
                    if [ -f "$python_directory/PSI_death_doselines.py" ]; then
                        python "$python_directory/PSI_death_doselines.py"
                        echo "Figure $arg Generated!"
                    elif [ -f "$python_directory/PSI_death_doselines.py" ]; then
                        python3 "$python_directory/PSI_death_doselines.py"
                        echo "Figure $arg Generated!"
                    else
                        echo "Python file (PSI_death_doselines.py) not found."
                    fi

                else
                    echo "Java file (DVR_CCR_Scatter.java) not found."
                fi
                ;;
            *)
                echo "Invalid argument: $arg"
                ;;
        esac
    done
else
    echo "Java file (ModelComparison.java) not found."
fi

# Delete all .csv files
find . -maxdepth 1 -type f -name "*.csv" -delete
echo "All .csv files deleted."

# Delete all .class files
find . -maxdepth 1 -type f -name "*.class" -delete
echo "All .class files deleted."