#!/bin/bash 
#SBATCH --qos=part2d
#SBATCH --partition=small
module load jdk8_32
java -jar OnCLMOEAD_R4.jar