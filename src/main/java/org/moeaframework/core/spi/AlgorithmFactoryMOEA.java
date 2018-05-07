/* Copyright 2009-2018 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.moeaframework.core.spi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.moeaframework.algorithm.StandardAlgorithms;
import org.moeaframework.core.Problem;
import org.moeaframework.core.AlgorithmMOEA;

/**
 * Factory for creating algorithm instances. See {@link AlgorithmProviderMOEA} for
 * details on adding new providers.
 * <p>
 * This class is thread safe.
 */
public class AlgorithmFactoryMOEA {

    /**
     * The static service loader for loading algorithm providers.
     */
    private static final ServiceLoader<AlgorithmProviderMOEA> PROVIDERS;

    /**
     * The default algorithm factory.
     */
    private static AlgorithmFactoryMOEA instance;

    /**
     * Collection of providers that have been manually added.
     */
    private List<AlgorithmProviderMOEA> customProviders;

    /**
     * Instantiates the static {@code PROVIDERS} and {@code instance} objects.
     */
    static {
        PROVIDERS = ServiceLoader.load(AlgorithmProviderMOEA.class);
        instance = new AlgorithmFactoryMOEA();
    }

    /**
     * Returns the default algorithm factory.
     *
     * @return the default algorithm factory
     */
    public static synchronized AlgorithmFactoryMOEA getInstance() {
        return instance;
    }

    /**
     * Sets the default algorithm factory.
     *
     * @param instance the default algorithm factory
     */
    public static synchronized void setInstance(AlgorithmFactoryMOEA instance) {
        AlgorithmFactoryMOEA.instance = instance;
    }

    /**
     * Constructs a new algorithm factory.
     */
    public AlgorithmFactoryMOEA() {
        super();

        customProviders = new ArrayList<AlgorithmProviderMOEA>();
    }

    /**
     * Adds an algorithm provider to this algorithm factory. Subsequent calls to
     * {@link #getAlgorithm(String, Properties, Problem)} will search the given
     * provider for a match.
     *
     * @param provider the new algorithm provider
     */
    public void addProvider(AlgorithmProviderMOEA provider) {
        customProviders.add(provider);
    }

    /**
     * Searches through all discovered {@code AlgorithmProviderMOEA} instances,
     * returning an instance of the algorithm with the registered name. The
     * algorithm is initialized using implementation-specific properties. This
     * method must throw an {@link ProviderNotFoundException} if no suitable
     * algorithm is found.
     *
     * @param name the name identifying the algorithm
     * @param properties the implementation-specific properties
     * @param problem the problem to be solved
     * @return an instance of the algorithm with the registered name
     * @throws ProviderNotFoundException if no provider for the algorithm is
     * available
     */
    public synchronized AlgorithmMOEA getAlgorithm(String name,
            Properties properties, Problem problem) {
        boolean hasStandardAlgorithms = false;

        // loop over all providers that have been manually added
        for (AlgorithmProviderMOEA provider : customProviders) {
            AlgorithmMOEA algorithm = instantiateAlgorithm(provider, name,
                    properties, problem);

            if (provider.getClass() == StandardAlgorithms.class) {
                hasStandardAlgorithms = true;
            }

            if (algorithm != null) {
                return algorithm;
            }
        }

        // loop over all providers available via the SPI
        Iterator<AlgorithmProviderMOEA> iterator = PROVIDERS.iterator();

        while (iterator.hasNext()) {
            AlgorithmProviderMOEA provider = iterator.next();
            AlgorithmMOEA algorithm = instantiateAlgorithm(provider, name,
                    properties, problem);

            if (provider.getClass() == StandardAlgorithms.class) {
                hasStandardAlgorithms = true;
            }

            if (algorithm != null) {
                return algorithm;
            }
        }

        // always ensure we check the standard algorithms
        System.out.println("has standard algorithm = " + hasStandardAlgorithms);
        if (!hasStandardAlgorithms) {
            AlgorithmMOEA algorithm = instantiateAlgorithm(
                    new StandardAlgorithms(), name, properties, problem);

            if (algorithm != null) {
                return algorithm;
            }
        }

        throw new ProviderNotFoundException(name);
    }

    /**
     * Attempts to instantiate the given algorithm using the given provider.
     *
     * @param provider the algorithm provider
     * @param name the name identifying the algorithm
     * @param properties the implementation-specific properties
     * @param problem the problem to be solved
     * @return an instance of the algorithm with the registered name; or
     * {@code null} if the provider does not implement the algorithm
     */
    private AlgorithmMOEA instantiateAlgorithm(AlgorithmProviderMOEA provider,
            String name, Properties properties, Problem problem) {
        try {
            return provider.getAlgorithm(name, properties, problem);
        } catch (ServiceConfigurationError e) {
            System.err.println(e.getMessage());
        }

        return null;
    }

}
