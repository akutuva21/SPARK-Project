#!/bin/bash

# Get the parent directory path
parent_directory="$(dirname "$(pwd)")"

# Specify the path to the desired directory one level outside
python_directory="$parent_directory/Python Files"

if [ -f "ModelComparison.java" ]; then
    javac ModelComparison.java
    java ModelComparison

    # Run the Python file
    if [ -f "$python_directory/Sample_Graphing.py" ]; then
        python "$python_directory/Sample_Graphing.py"
        echo "Figure 1 Generated!"
    elif [ -f "$python_directory/Sample_Graphing.py" ]; then
        python3 "$python_directory/Sample_Graphing.py"
        echo "Figure 1 Generated!"
    else
        echo "Python file (Sample_Graphing.py) not found."
    fi

    if [ -f "$python_directory/Death_Sweep.py" ]; then
        python "$python_directory/Death_Sweep.py"
        echo "Figure 2 Generated!"
    elif [ -f "$python_directory/Death_Sweep.py" ]; then
        python3 "$python_directory/Death_Sweep.py"
        echo "Figure 2 Generated!"
    else
        echo "Python file (Death_Sweep.py) not found."
    fi

    if [ -f "$python_directory/Lambda_Sweep.py" ]; then
        python "$python_directory/Lambda_Sweep.py"
        echo "Figure 3 Generated!"
    elif [ -f "$python_directory/Lambda_Sweep.py" ]; then
        python3 "$python_directory/Lambda_Sweep.py"
        echo "Figure 3 Generated!"
    else
        echo "Python file (Lambda_Sweep.py) not found."
    fi

    if [ -f "$python_directory/Death_PSI_Cumul_Graphs.py" ]; then
        python "$python_directory/Death_PSI_Cumul_Graphs.py" "direct"
        echo "Figure 4 Generated!"
        python "$python_directory/Death_PSI_Cumul_Graphs.py" "indirect"
        echo "Figure 5 Generated!"
    elif [ -f "$python_directory/Death_PSI_Cumul_Graphs.py" ]; then
        python3 "$python_directory/Death_PSI_Cumul_Graphs.py" "direct"
        echo "Figure 4 Generated!"
        python3 "$python_directory/Death_PSI_Cumul_Graphs.py" "indirect"
        echo "Figure 5 Generated!"
    else
        echo "Python file (Death_PSI_Cumul_Graphs.py) not found."
    fi

else
    echo "Java file (ModelComparison.java) not found."
fi

# Delete all .csv files
find . -maxdepth 1 -type f -name "*.csv" -delete
echo "All .csv files deleted."

# Delete all .class files
find . -maxdepth 1 -type f -name "*.class" -delete
echo "All .class files deleted."