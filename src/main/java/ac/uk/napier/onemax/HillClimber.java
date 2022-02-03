package ac.uk.napier.onemax;

public class HillClimber extends  Algorithm{

	
	
	@Override
	public void run() {
		best = new Individual();	
		startingFitness = best.getFitness();
		
		int improv = 0;;
		running = true;
		
		String[] filenames = FileGetter.getFileNames("", "", ".ind");
		if(seed && filenames != null && filenames.length > 0) {
			String filename = filenames[filenames.length - 1];		
			best = Serialize.load(filename);
		}
		int optimal = Individual.getOptimal().getFitness();
		while(running) {			
			Individual candidate = mutate(best.copy());
			if(candidate.evaluateFitness() > best.getFitness()) {
				best = candidate;				
				if(best.getFitness() == optimal) {					
					running = false;
				}				
				improv++;
				if(improv % 250 == 0) {
					Serialize.save(best, System.currentTimeMillis() + ".ind");
				}
			}
			setChanged();
			notifyObservers(best);
			System.out.println(Individual.getEvaluations() + "\t" + best.getFitness() + "\t" + optimal);			
		}
	}

	private Individual mutate(Individual candidate) {
		int x = parameters.getRandom().nextInt(parameters.getWidth());
		int y = parameters.getRandom().nextInt(parameters.getHeight());		
		candidate.getChromosome()[y][x] = !candidate.getChromosome()[y][x]; 		
		return candidate;
	}

	@Override
	public String toString() {
		return "Hill Climber";
	}
}
