#!/bin/bash 
#SBATCH --qos=part2d
#SBATCH --partition=large
module load jdk8_32
java -jar OffATMOEAD_R4.jar