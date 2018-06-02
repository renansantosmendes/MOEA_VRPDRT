#!/bin/bash 
#SBATCH --qos=part2d
#SBATCH --partition=small
module load jdk8_32
java -jar OffCLMOEAD_R3.jar