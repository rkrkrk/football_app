package fm.footballstats.db.run;

public class Shot {
	private String team;
	private String name;
	private boolean play;
	private int goals;
	private int total;

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	private int points;
	private int wides;
	private int misses;

	public Shot() {
		// TODO Auto-generated constructor stub
	}

	public Shot(String team, String name, boolean play, int total, int goals,
			int points, int wides, int misses) {
		super();
		this.team = team;
		this.name = name;
		this.play = play;
		this.total = total;
		this.goals = goals;
		this.points = points;
		this.wides = wides;
		this.misses = misses;
	}

	public void updateTotal(int total) {
		this.total += total;
	}

	public void updateGoals(int goals) {
		this.goals += goals;
	}

	public void updatePoints(int points) {
		this.points += points;
	}

	public void updateWides(int wides) {
		this.wides += wides;
	}

	public void updateMisses(int misses) {
		this.misses += misses;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isPlay() {
		return play;
	}

	public void setPlay(boolean play) {
		this.play = play;
	}

	public int getGoals() {
		return goals;
	}

	public void setGoals(int goals) {
		this.goals = goals;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getWides() {
		return wides;
	}

	public void setWides(int wides) {
		this.wides = wides;
	}

	public int getMisses() {
		return misses;
	}

	public void setMisses(int misses) {
		this.misses = misses;
	}

}
