import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

class Chromosome {

    /**
     * The list of cities, which are the genes of this chromosome.
     */
    public int[] cityList;

    /**
     * The cost of following the cityList order of this chromosome.
     */
    public double cost;

    /**
     * @param cities The order that this chromosome would visit the cities.
     */
    
    Chromosome(City[] cities) {
        Random generator = new Random();
        cityList = new int[cities.length];

        ArrayList<Integer> visitedCities = new ArrayList<Integer>();
        int currentCity = generator.nextInt(cities.length);
        visitedCities.add(currentCity);
        cityList[0] = currentCity;
        
        for (int i = 1; i < cities.length; i++) {
            int[] distances = cities[currentCity].proximity(cities);
            int index = 0;
            int min = 1000000;
            for (int j = 1; j < distances.length; j++) {
                if (distances[j] < min && !visitedCities.contains(j) && distances[j] != 0) {
                    min = distances[j];
                    index = j;
                }
            }
            currentCity = index;
            visitedCities.add(currentCity);
            cityList[i] = currentCity;
        }
        calculateCost(cities);
    }
    
    Chromosome(Chromosome other) {
        this.cityList = other.cityList.clone();
        this.cost = other.cost;
    }
    
    Chromosome(int[] cityList) {
        this.cityList = new int[cityList.length];
    }
    
    public static boolean valid(Chromosome chromosome) {
        int[] cityList = chromosome.getCities().clone();
        ArrayList<Integer> visitedCities = new ArrayList<Integer>();
        for (int i = 0; i < cityList.length; i++) {
            if (visitedCities.contains(cityList[i])) {
                return false;
            }
            visitedCities.add(cityList[i]);
        }
        
        return true;
    }
    
    

    /**
     * Calculate the cost of the specified list of cities.
     *
     * @param cities A list of cities.
     */
    void calculateCost(City[] cities) {
        this.cost = 0;
        for (int i = 0; i < this.cityList.length - 1; i++) {
            double dist = cities[this.cityList[i]].proximity(cities[this.cityList[i + 1]]);
            this.cost += dist;
        }

        this.cost += cities[this.cityList[0]].proximity(cities[this.cityList[this.cityList.length - 1]]); //Adding return home
    }

    /**
     * Get the cost for this chromosome. This is the amount of distance that
     * must be traveled.
     */
    double getCost() {
        return cost;
    }

    /**
     * @param i The city you want.
     * @return The ith city.
     */
    int getCity(int i) {
        return cityList[i];
    }
    
    /**
     * @return the list representing the tour of this chromosome
     */
    int[] getCities() {
        return cityList;
    }

    /**
     * Set the order of cities that this chromosome would visit.
     *
     * @param list A list of cities.
     */
    void setCities(int[] list) {
        for (int i = 0; i < cityList.length; i++) {
            cityList[i] = list[i];
        }
    }

    /**
     * Set the index'th city in the city list.
     *
     * @param index The city index to change
     * @param value The city number to place into the index.
     */
    void setCity(int index, int value) {
        this.cityList[index] = value;
    }

    /**
     * Sort the chromosomes by their cost.
     *
     * @param chromosomes An array of chromosomes to sort.
     * @param num         How much of the chromosome list to sort.
     */
    public static void sortChromosomes(Chromosome chromosomes[], int num) {
        Chromosome ctemp;
        boolean swapped = true;
        while (swapped) {
            swapped = false;
            for (int i = 0; i < num - 1; i++) {
                if (chromosomes[i].getCost() > chromosomes[i + 1].getCost()) {
                    ctemp = chromosomes[i];
                    chromosomes[i] = chromosomes[i + 1];
                    chromosomes[i + 1] = ctemp;
                    swapped = true;
                }
            }
        }
    }
    
    /**
     * Swap a random point with the shortest possible mutation
     *
     * @param list A list of cities.
     */
    public void greedyMutate(City[] cities) {
        Random generator = new Random();
        
        int point = generator.nextInt(cityList.length);
        int[] distances = cities[cityList[point]].proximity(cities);
        int min  = 10000;
        int city = 0;
        for (int j = 0; j < distances.length; j++) {
                if (distances[j] < min && distances[j] != 0) {
                    min = distances[j];
                    city = j;
                }
        }
        int index = -1;
        for (int i = 0; i < cityList.length; i++) {
            if (cityList[i] == city) {
                index = i;
                break;
            }
        }
        
        int temp = this.getCity((point + 1)%cityList.length);
        this.cityList[(point + 1)%cityList.length] =  cityList[index];
        this.cityList[index] =  temp;
        
    }
    
    /**
     * Randomly shuffle points in the city list with each other
     *
     * @param prob list the probability of shuffling 
     */
    public void shuffleMutate(double prob) {
        Random generator = new Random();
        
        for (int i = 0; i < cityList.length; i++) {
            double p = generator.nextDouble();
            
            if (p < prob) {
                int mutIndex = generator.nextInt(cityList.length);
                int temp = this.cityList[i];
                this.cityList[i] = this.cityList[mutIndex];
                this.cityList[mutIndex] = temp;
            }
        }
    }
    
    /**
     * Swap a random points in the list with a mirror point from the opposite end of the list. For example, in a list of length N, the i'th point will swap with the (n-i)'th point
     *
     */
    public void inversionMutate() {
        Random generator = new Random();
        
        int startPoint = generator.nextInt(this.cityList.length - 1);
        int endPoint = startPoint + generator.nextInt(this.cityList.length - startPoint - 1);
        
        for (int i = 0; i <= Math.floor((endPoint - startPoint)/2); i++ ) {
            int temp = this.getCity(startPoint + i);
            int swap = this.getCity(endPoint - i);
            this.cityList[startPoint + i] =  swap;
            this.cityList[endPoint - i] =  temp;
        }
    }
    
    /**
     * Standard mutation. Randomly swap two points.
     */
    public void transpositionMutate() {
        Random generator = new Random();
        
        int firstPoint = generator.nextInt(this.cityList.length - 1);
        int secondPoint = generator.nextInt(this.cityList.length - 1);
        
        int temp = this.getCity(secondPoint);
        this.setCity(secondPoint, this.getCity(firstPoint));
        this.setCity(firstPoint, temp);
        
    }
    
    /**
     * Similar to transpose mutate where two random points are swapped, however instead of a single point, a segment of the list of city traversals is 'cut and pasted' to a new location in the list
     */
    public void translocationMutate() {
        Random generator = new Random();
        
        int chosenPoint = generator.nextInt(this.cityList.length - 1);
        int insertionPoint = generator.nextInt(this.cityList.length - 1);
        
        int temp = cityList[chosenPoint];
        
        if (chosenPoint < insertionPoint) {
            for (int i = chosenPoint; i < insertionPoint; i++) {
                cityList[i] = cityList[i+1];
            }
            
            cityList[insertionPoint] = temp;
        } else {
            for (int i = chosenPoint; i > insertionPoint; i--) {
                cityList[i] = cityList[i-1];
            }
            
            cityList[insertionPoint] = temp;
        }
    }
    
    
    /**
	 * Standard crossover operator for TSP. We crossover by selecting a random crossover point and cutting the lists into two new children. 
	 * However, we increase the likelihood by generating children as follows:
	 * We iterate through one child after crossover, checking if cities in the second part of the new child appear in the first part of the new child.
	 * If a city has been visitied already, we look back to the first parent and take that city, also looking if it hasn't been visited yet. We repeat this as desired.
	 * There is still a chance that this causes some repeated cities to be introduced, which will be pruned later when doing validation checks.
     *  TODO: need to fix tournament selection and this thingy; do elitsm and then the tournament
     */
    public static Chromosome sequentialCrossover(City[] cities, Chromosome parent1, Chromosome parent2) {
        int[] parentCities1 = parent1.getCities();
        int[] parentCities2 = parent2.getCities();
        
        ArrayList<Integer> visitedCities = new ArrayList<Integer>();

        Chromosome child = new Chromosome(parentCities1);
        
        child.setCity(0, parentCities1[0]);
        visitedCities.add(parentCities1[0]);
        int[] nextCities = {-1, -1};
        
        for (int i = 0; i < cities.length - 1; i++) {
            int currentCity = child.getCity(i);
            boolean legitimate = false;
            int currentPos = -1;
            
            for (int j = 0; j < cities.length; j++) {
                if (parentCities1[j] == currentCity) {
                    currentPos = j;
                    break;
                }
            }
            
            for (int j = currentPos; j < cities.length; j++) {
                if (! (visitedCities.contains(parentCities1[j])) ) {
                    legitimate = true;
                    nextCities[0] = parentCities1[j];
                }
            }
            
            if (! legitimate) {
                for (int j = 0; j < cities.length; j++) {
                    if (!(visitedCities.contains(j)) && !(currentCity == j)) {
                        legitimate = true;
                        nextCities[0] = j;
                    }
                }
            }   
            
            currentCity = child.getCity(i);
            legitimate = false;
            currentPos = -1;
            
            for (int j = 0; j < cities.length; j++) {
                if (parentCities2[j] == currentCity) {
                    currentPos = j;
                    break;
                }
            }
            
            for (int j = currentPos; j < cities.length; j++) {
                if (! (visitedCities.contains(parentCities2[j])) ) {
                    legitimate = true;
                    nextCities[1] = parentCities2[j];
                }
            }
            
            if (! legitimate) {
                for (int j = 0; j < cities.length; j++) {
                    if (!(visitedCities.contains(j)) && !(currentCity == j)) {
                        legitimate = true;
                        nextCities[1] = j;
                    }
                }
            }
            
            if (cities[nextCities[0]].proximity(cities[currentCity]) < cities[nextCities[1]].proximity(cities[currentCity])) {
                child.setCity(i + 1, nextCities[0]);
                visitedCities.add(nextCities[0]);
            } else {
                child.setCity(i + 1, nextCities[1]);
                visitedCities.add(nextCities[1]);
            }
            
        }
        
        return child;
    }
}
