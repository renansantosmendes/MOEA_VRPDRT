#!/bin/bash 
#SBATCH --qos=part2d
#SBATCH --partition=large
module load jdk8_32
java -jar OffCLMOEAD_R6.jar