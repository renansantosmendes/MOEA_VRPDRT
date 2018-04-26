#!/bin/bash 
#SBATCH --qos=part1d
#SBATCH --partition=large
module load jdk8_32
java -jar CLMOEAD_R2.jar