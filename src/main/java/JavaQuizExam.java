import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.*;


public class JavaQuizExam {
    private static final String Users_fileLocation="./src/main/resources/users.json";
    private static final String Quiz_fileLocation= "./src/main/resources/quiz.json";
    private static final int Qsn_Number=10;


    public static void main(String[] args) throws IOException, ParseException {
        Scanner scan=new Scanner(System.in);
        System.out.println("System:> Enter Your Username: ");
        String username= scan.nextLine();
        System.out.println("System:> Enter Your Password: ");
        String password= scan.nextLine();

        JSONObject user= authenticateUser(username,password);
        if(user==null){
            System.out.println("System:> Invalid Username or Password.");
            return;
        }

        String role= (String) user.get("role");
        if (role.equals("admin")) {
            System.out.println("Welcome admin! Please create new questions in the question bank.");
            addQuestions(scan);
        } else if (role.equals("student")) {
            System.out.println("System:> Welcome "+ username+" to the quiz! We will throw you "+Qsn_Number+" questions. Each MCQ mark is 1 and no negative marking. Are you ready? Press 's' to start.");
            String input=scan.nextLine();
            if(input.equals("s")){
                List<JSONObject> quizQuestions = getQuizQuestions(Qsn_Number);
                int score =startQuiz(scan, quizQuestions);
                displayResult(score);
            }
        }
    }

    private static JSONObject authenticateUser(String username, String password) throws IOException, ParseException {
        JSONParser parser=new JSONParser();
        JSONArray users= (JSONArray) parser.parse(new FileReader(Users_fileLocation));
        for(Object obj: users){
            JSONObject user = (JSONObject) obj;
            String Username= user.get("username").toString();
            String Password= user.get("password").toString();

            if(username.equals(Username) && password.equals(Password)){
                return user;
            }
        }
        return null;
    }

    private static void addQuestions(Scanner scan) throws IOException, ParseException {
        JSONParser parser=new JSONParser();
        JSONArray jsonArray= new JSONArray();
        jsonArray= (JSONArray) parser.parse(new FileReader(Quiz_fileLocation));
        System.out.println("Total Questions: "+jsonArray.size());

        while(true){
            JSONObject question=new JSONObject();
            System.out.println("System:> Input your question");
            System.out.print("Admin: ");
            String qsnText=scan.nextLine();
            System.out.println("System:> Input option 1:");
            System.out.print("Admin: ");
            String option1=scan.nextLine();
            System.out.println("System:> Input option 2:");
            System.out.print("Admin: ");
            String option2=scan.nextLine();
            System.out.println("System:> Input option 3:");
            System.out.print("Admin: ");
            String option3=scan.nextLine();
            System.out.println("System:> Input option 4:");
            System.out.print("Admin: ");
            String option4=scan.nextLine();
            System.out.println("System:> What is the answer key?");
            System.out.print("Admin: ");
            int answerKey=scan.nextInt();

            question.put("question", qsnText);
            question.put("option 1",option1);
            question.put("option 2",option2);
            question.put("option 3",option3);
            question.put("option 4",option4);
            question.put("answerKey",answerKey);

            jsonArray.add(question);

            System.out.println("System:> Saved successfully! Do you want to add more questions? (press 's' for start and 'q' for quit)");
            scan.nextLine();
            String input=scan.nextLine();
            if(input.equals("q")){
                break;
            }else if(input.equals("s")){
                //FileWriter writer=new FileWriter(Quiz_fileLocation);
                //writer.write(jsonArray.toJSONString());
                //writer.flush();
                continue;
            }
        }

        FileWriter writer=new FileWriter(Quiz_fileLocation);
        writer.write(jsonArray.toJSONString());
        writer.flush();//save question
        writer.close();
    }

    private static List<JSONObject> getQuizQuestions(Integer qsn_Number) throws IOException, ParseException {
        JSONParser parser=new JSONParser();
        List<JSONObject> quizQsn= new ArrayList<>();
        JSONArray qsnArray= (JSONArray) parser.parse(new FileReader(Quiz_fileLocation));
        int totalQuestions = qsnArray.size();
        if(totalQuestions<qsn_Number){
            System.out.println("System:> Insufficient questions in the quiz bank. Please add more questions.");
            return quizQsn;
        }

        Random random=new Random();
        Set<Integer> selectedQuestionIndex = new HashSet<>();

        while(selectedQuestionIndex.size()<qsn_Number){
            int randomNumberInRange = random.nextInt(qsn_Number); // Generates random number
            if(!selectedQuestionIndex.contains(randomNumberInRange)){
                JSONObject question = (JSONObject) qsnArray.get(randomNumberInRange);
                quizQsn.add(question);
                selectedQuestionIndex.add(randomNumberInRange);
            }
        }
        return quizQsn;
    }

    private static int startQuiz(Scanner scanner, List<JSONObject> quizQuestions){
        int score = 0;
        int questionNum = 1;
        for (JSONObject question : quizQuestions){
            System.out.println("[Question " + questionNum + "] " + question.get("question"));
            System.out.println("1. " + question.get("option 1"));
            System.out.println("2. " + question.get("option 2"));
            System.out.println("3. " + question.get("option 3"));
            System.out.println("4. " + question.get("option 4"));

            System.out.print("Student:> ");
            int userAnswer =scanner.nextInt();
            long answerKey= (long) question.get("answerKey");
            if(userAnswer==answerKey){
                score++;
            }
            questionNum++;
        }
        return score;
    }

    private static void displayResult(int score) throws IOException, ParseException {
        System.out.println("Quiz has been completed successfully!");

        if(score>=8){
            System.out.println("Excellent! You have got "+score+" out of "+Qsn_Number);
        } else if (score<8 && score>=5) {
            System.out.println("Good! You have got "+score+" out of "+Qsn_Number);
        }else if (score<5 && score>=2) {
            System.out.println("Very poor! You have got "+score+" out of "+Qsn_Number);
        }else if (score<2 && score==0) {
            System.out.println("Very sorry you are failed. You have got "+score+" out of "+Qsn_Number);
        }

        Scanner scan=new Scanner(System.in);
        System.out.println("Would you like to start again? Press 's' for start or 'q' for quit");
        String input= scan.nextLine();
        if(input.equals("s")) {
            System.out.println();
            main(null);
        }
    }

}
