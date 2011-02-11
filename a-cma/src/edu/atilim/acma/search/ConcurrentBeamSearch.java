package edu.atilim.acma.search;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import edu.atilim.acma.RunConfig;
import edu.atilim.acma.concurrent.Instance;
import edu.atilim.acma.concurrent.InstanceSet;
import edu.atilim.acma.design.Design;

public class ConcurrentBeamSearch extends ConcurrentAlgorithm {
	private int beamLength;
	private int randomDepth;
	private int runCount;
	private int iterations;
	
	public ConcurrentBeamSearch() {
	}

	public ConcurrentBeamSearch(String name, RunConfig config, Design initialDesign, int beamLength, int randomDepth, int iterations, int runCount) {
		super(name, config, initialDesign);
		
		this.beamLength = beamLength;
		this.randomDepth = randomDepth;
		this.runCount = runCount;
		this.iterations = iterations;
	}
	
	@Override
	public void runMaster(InstanceSet instances) {
		for (int runs = 0; runs < runCount; runs++) {
			HashMap<UUID, Design> population = new HashMap<UUID, Design>();
			
			System.out.printf("Generating %d random designs for initial population.\n", beamLength);
			SolutionDesign initial = new SolutionDesign(getInitialDesign(), getConfig());
			for (int i = 0; i < beamLength; i++) {
				SolutionDesign random = initial.getRandomNeighbor(randomDepth);
				population.put(random.getDesign().getId(), random.getDesign());
			}
			System.out.println("Generated initial population.");
			
			for (int i = 0 ; i < iterations; i++) {
				System.out.printf("Starting iteration %d.\n", i + 1);
				instances.broadcast(Boolean.TRUE);
				expandPopulationMaster(instances, population);
			}
			
			SolutionDesign best = initial;
			for (Design d : population.values()) {
				SolutionDesign sd = new SolutionDesign(d, getConfig());
				if (sd.isBetterThan(best))
					best = sd;
			}
			
			System.out.printf("Finished %d iterations. Found best design with score: %.6f.\n", iterations, best.getScore());
			
			onFinish(best.getDesign());
		}
		
		instances.broadcast(Boolean.FALSE);
	}
	
	private void expandPopulationMaster(InstanceSet instances, HashMap<UUID, Design> population) {
		System.out.println("Scattering population to instances.");
		instances.scatter(new ArrayList<Design>(population.values()));
		System.out.println("Waiting for population expansion.");
		
		ArrayList<Double> scores = instances.gather(Double.class);
		System.out.printf("Received %d scores.\n", scores.size());
		Collections.sort(scores);
		Double beamcut = scores.get(Math.min(scores.size() - 1, beamLength - 1));
		System.out.printf("Beam cut at %.6f.\n", beamcut);
		
		instances.broadcast(beamcut);
		System.out.println("Waiting for new population");
		ArrayList<Design> newpop = instances.gather(Design.class);
		population.clear();
		for (Design d : newpop) {
			population.put(d.getId(), d);
		}
		System.out.printf("New population generated with %d designs. Best: %.6f\n", newpop.size(), scores.get(0));
	}

	@Override
	public void runWorker(Instance master) {
		while(master.receive(Boolean.class))
			expandPopulationWorker(master);
		
		System.out.println("Finalizing Instance.");
	}
	
	@SuppressWarnings("unchecked")
	private void expandPopulationWorker(Instance master) {
		System.out.println("Waiting for population");
		ArrayList<Design> designs = (ArrayList<Design>)master.receive();
		System.out.printf("Received %d designs. Expanding neighbors.\n", designs.size());
		
		SortedSet<FoundDesign> neighbors = new TreeSet<FoundDesign>();
		
		for (Design d : designs) {
			SolutionDesign design = new SolutionDesign(d, getConfig());
			
			for (SolutionDesign neighbor : design) {
				neighbors.add(new FoundDesign(neighbor.getScore(), neighbor.getDesign()));
				
				if (neighbors.size() > beamLength) {
					neighbors.remove(neighbors.first());
				}
			}
		}

		ArrayList<Double> scores = new ArrayList<Double>();
		for (FoundDesign fd : neighbors)
			scores.add(fd.score);
		master.send(scores);
		
 		Double beamcut = master.receive(Double.class);
		
		designs.clear();
		for (FoundDesign d : neighbors) {
			if (d.score <= beamcut)
				designs.add(d.design);
		}
		master.send(designs);
	}
	
	private static class FoundDesign implements Comparable<FoundDesign> {
		private double score;
		private Design design;

		private FoundDesign(double score, Design design) {
			this.score = score;
			this.design = design;
		}

		@Override
		public int compareTo(FoundDesign o) {
			return Double.compare(o.score, score);
		}
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		
		out.writeInt(0); //version
		out.writeInt(beamLength);
		out.writeInt(randomDepth);
		out.writeInt(runCount);
		out.writeInt(iterations);
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		
		in.readInt();
		beamLength = in.readInt();
		randomDepth = in.readInt();
		runCount = in.readInt();
		iterations = in.readInt();
	}
}