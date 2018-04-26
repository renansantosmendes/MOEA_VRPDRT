/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.moeaframework.algorithm;

import java.util.Properties;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.Variation;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.spi.AlgorithmProvider;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.util.TypedProperties;

/**
 *
 * @author renansantos
 */
public class CLMOEADProvider extends AlgorithmProvider {

    @Override
    public Algorithm getAlgorithm(String name, Properties properties, Problem problem) {

        TypedProperties typedProperties = new TypedProperties(properties);
        if (name.equalsIgnoreCase("CLMOEAD") || name.equalsIgnoreCase("CLMOEA/D")) {
            return newCLMOEAD(typedProperties, problem);
        } else {
            return null;
        }

    }

    private Algorithm newCLMOEAD(TypedProperties properties, Problem problem) {
        int populationSize = (int) properties.getDouble("populationSize", 100);

        //enforce population size lower bound
        if (populationSize < problem.getNumberOfObjectives()) {
            System.err.println("increasing CLMOEA/D population size");
            populationSize = problem.getNumberOfObjectives();
        }

        Initialization initialization = new RandomInitialization(problem,
                populationSize);

        //default to de+pm for real-encodings
        String operator = properties.getString("operator", null);

        if ((operator == null) && checkType(RealVariable.class, problem)) {
            operator = "de+pm";
        }

        Variation variation = OperatorFactory.getInstance().getVariation(
                operator, properties, problem);

        int neighborhoodSize = 20;
        int eta = 2;

        if (properties.contains("neighborhoodSize")) {
            neighborhoodSize = Math.max(2,
                    (int) (properties.getDouble("neighborhoodSize", 0.1)
                    * populationSize));
        }

        if (neighborhoodSize > populationSize) {
            neighborhoodSize = populationSize;
        }

        if (properties.contains("eta")) {
            eta = Math.max(2, (int) (properties.getDouble("eta", 0.01)
                    * populationSize));
        }

        CLMOEAD algorithm = new CLMOEAD(
                problem,
                properties.getProperties().getProperty("instance"),
                properties.getInt("clusters", 2),
                properties.getProperties().getProperty("filePath"),
                neighborhoodSize,
                null,
                initialization,
                variation,
                properties.getDouble("delta", 0.9),
                eta,
                (int) properties.getDouble("updateUtility", -1));

        return algorithm;
    }
    
    private boolean checkType(Class<? extends Variable> type, Problem problem) {
        Solution solution = problem.newSolution();

        for (int i = 0; i < solution.getNumberOfVariables(); i++) {
            if (!type.isInstance(solution.getVariable(i))) {
                return false;
            }
        }

        return true;
    }

}
