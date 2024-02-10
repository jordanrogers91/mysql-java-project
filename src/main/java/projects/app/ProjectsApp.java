package projects.app;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {
	private Scanner scanner = new Scanner(System.in);

	private ProjectService projectService = new ProjectService();
	private Project curProject;
	// instance variable to hold the list of operations
	// @formatter:off
	private List<String> operations = List.of( 
			"1) Add a project",
			"2) List projects",
			"3) Select project",
			"4) Update project details",
			"5) Delete a project"
			
			);
	// @formatter:on			

	public static void main(String[] args) {
		// call the method that processes the menu onto a new projects object
		new ProjectsApp().processUserSelections();

	} // end of main method

	// this method displays the menu selection, gets a selection from the user, and
	// acts on the selection made.
	// instantiate done variable to false
	private void processUserSelections() {
		boolean done = false;
		// loop through while not done
		while (!done) {
			try {
				int selection = getUserSelection();
				// since the user input is an int we can use a switch statement to process the
				// users selection
				switch (selection) {
				case -1:
					done = exitMenu();
					break;
				case 1:
					createProject();
					break;
				case 2:
					listProjects();
					break;
				case 3:
					selectProject();
					break;
				case 4:
					updateProjectDetails();
					break;
				case 5:
					deleteProject();
					break;
				default:
					System.out.println("\n" + selection + " is not a valid selection. Try again.");
					break;
				}
			} catch (Exception e) {
				System.out.println("\nError: " + e + " Try again");
				// by concatenating the exception object into a String literal Java implicitly
				// calls the toString() method which represents objects as strings.
			}
		}
	} // end of processUserSelections method

	private void deleteProject() {
		listProjects();
		
		Integer projectId = getIntInput("Enter the Project ID you wish to delete.");
		
		projectService.deleteProject(projectId);
		System.out.println("Project " + projectId + " was deleted successfully.");
		
		if(Objects.nonNull(curProject) && curProject.getProjectId().equals(projectId)) {
			curProject = null;
		}
	}

	private void updateProjectDetails() {
		if(Objects.isNull(curProject)) {
			System.out.println("\nPlease select a project.");
			return;
		}
		String projectName = getStringInput("Enter the project name [" + curProject.getProjectName() + "]");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours [" + curProject.getEstimatedHours() + "]");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours [" + curProject.getActualHours() + "]");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5) [" + curProject.getDifficulty() + "]");
		String notes = getStringInput("Enter the project notes [" + curProject.getNotes() + "]");
		
		Project project = new Project();
		
		project.setProjectId(curProject.getProjectId());
		project.setProjectName(Objects.isNull(projectName) ? curProject.getProjectName() : projectName);
		project.setEstimatedHours(Objects.isNull(estimatedHours) ? curProject.getEstimatedHours() : estimatedHours);
		project.setActualHours(Objects.isNull(actualHours) ? curProject.getActualHours() : actualHours);
		project.setDifficulty(Objects.isNull(difficulty) ? curProject.getDifficulty() : difficulty);
		project.setNotes(Objects.isNull(notes) ? curProject.getNotes() : notes);
		
		projectService.modifyProjectDetails(project);
		
		curProject = projectService.fetchProjectById(curProject.getProjectId());
	}

	private void selectProject() {
		listProjects();
		Integer projectId = getIntInput("Enter a project ID to select a project");
		// unselect the current project
		curProject = null;
		// this will throw an exception if an invalid projectId is entered
		curProject = projectService.fetchProjectById(projectId);
	}

	private void listProjects() {
		List<Project> projects = projectService.fetchAllProjects();

		System.out.println("\nProjects:");

		projects.forEach(
				project -> System.out.println("		" + project.getProjectId() + ": " + project.getProjectName()));

	} // end of listProjects method

	private void createProject() {
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimateHours = getDecimalInput("Enter the estimated hours.");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours.");
		Integer difficulty = getIntInput("Enter the projecct difficulty (1-5)");
		String notes = getStringInput("Enter project notes");
		// new variable initialized to a new Project object
		Project project = new Project();

		// call setters from project entity class to take values from user from above
		// and set them.
		project.setProjectName(projectName);
		project.setEstimatedHours(estimateHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);
		// Explanation for this line
		Project dbProject = projectService.addProject(project);
		System.out.println("You have successfully created project: " + dbProject);

	} // end of createProject method

	// this method will return a string and convert it to the appropriate BigDecimal
	// value.
	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);
		// test the value in the variable input if null then return null.
		if (Objects.isNull(input)) {
			return null;
		}
		// convert the value of the string to an Int
		try {
			// this creates a new BigDecimal object and sets the number of the decimal
			// places to 2.
			return new BigDecimal(input).setScale(2);
		} // end of try
			// if the conversion is not possible a NumberFormatException is thrown
		catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid decimal number.");
		} // end of catch
	} // end of getDecimalInput method

	private boolean exitMenu() {
		System.out.println("Exiting the menu");
		return true;
	} // end of

	// This method prints the operations and then accept user input.
	private int getUserSelection() {
		printOperations();
		Integer input = getIntInput("Enter a menu selection");
		return Objects.isNull(input) ? -1 : input;
		// The return statement checks to see if the value in the variable is not null
		// if it is i returns -1 which will exit the application, if not it will return
		// the value of the input. Reads as (if input is null (thing to do if true :
		// else thing to do if false)
	} // end of getUserSelection method

	// The safest way to get an input line is to input it as a String and then
	// convert it to the appropriate type. You will right this method to return an
	// Integer value.
	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);
		// test the value in the variable input if null then return null.
		if (Objects.isNull(input)) {
			return null;
		}
		// convert the value of the string to an Int
		try {
			return Integer.valueOf(input);
		} // end of try
			// if the conversion is not possible a NumberFormatException is thrown
		catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid number.");
		} // end of catch

	} // end of getIntInput method
		// method that really prints the prompt and gets input from the user. This is
		// the lowest level input method. The other input methods call this method.

	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String input = scanner.nextLine();
		// test the value of input. If blank return null else return the input trimmed.
		return input.isBlank() ? null : input.trim();
	}

	// This method prints each selection to the console
	private void printOperations() {
		System.out.println("\nThese are the available selections. Press the Enter key to quit:");
		// Expression to print list of operations. .forEach loops through operations and
		// then use a lamda expression for each line in operations takes the line
		// (parameter) to -> the expression.
		operations.forEach(line -> System.out.println("		" + line));
		if (Objects.isNull(curProject)) {
			System.out.println("\nYou are not working with a project.");
		} else {
			System.out.println("\nYou are working with project: " + curProject);
		}
	}// end of printOperations method

}
