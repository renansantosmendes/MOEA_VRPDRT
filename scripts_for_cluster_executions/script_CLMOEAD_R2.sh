#!/bin/bash 
#SBATCH --qos=part2d
#SBATCH --partition=large
module load jdk8_32
java -jar CLMOEAD_R2.jar