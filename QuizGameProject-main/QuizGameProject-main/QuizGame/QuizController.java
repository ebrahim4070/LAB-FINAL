import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.sql.*;
import java.util.*;

public class QuizController {

    @FXML private Label questionLabel, timerLabel, scoreLabel;
    @FXML private RadioButton opt1, opt2, opt3, opt4;

    private ToggleGroup optionGroup;

    private List<QuizModel> questions;
    private int currentQuestion = 0;
    private int score = 0;
    private Timeline timeline;
    private int timeLeft = 10;

    public void initialize() {
        optionGroup = new ToggleGroup();
        opt1.setToggleGroup(optionGroup);
        opt2.setToggleGroup(optionGroup);
        opt3.setToggleGroup(optionGroup);
        opt4.setToggleGroup(optionGroup);

        questions = loadRandomQuestions();
        if (questions.isEmpty()) {
            showAlert("কোনো প্রশ্ন পাওয়া যায়নি।");
            System.exit(0);
        }

        showQuestion();
        startTimer();
    }

    private void showQuestion() {
        if (currentQuestion < questions.size()) {
            QuizModel q = questions.get(currentQuestion);
            questionLabel.setText((currentQuestion + 1) + ". " + q.getQuestion());
            String[] options = q.getOptions();
            opt1.setText(options[0]);
            opt2.setText(options[1]);
            opt3.setText(options[2]);
            opt4.setText(options[3]);
            optionGroup.selectToggle(null);
            timeLeft = 10;
            timerLabel.setText("সময় বাকি: " + timeLeft + " সেকেন্ড");
        } else {
            askForNameAndSaveScore();
        }
    }

    @FXML
    private void handleNext() {
        checkAnswer();
        currentQuestion++;
        showQuestion();
    }

    private void checkAnswer() {
        RadioButton selected = (RadioButton) optionGroup.getSelectedToggle();
        if (selected == null) return;

        int selectedIndex = -1;
        if (selected == opt1) selectedIndex = 1;
        if (selected == opt2) selectedIndex = 2;
        if (selected == opt3) selectedIndex = 3;
        if (selected == opt4) selectedIndex = 4;

        if (selectedIndex == questions.get(currentQuestion).getCorrectOption()) {
            score += 38;
        }
    }

    private List<QuizModel> loadRandomQuestions() {
        List<QuizModel> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM questions WHERE topic = 'History' ORDER BY RAND() LIMIT 2"
            );
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String q = rs.getString("question_text");
                String[] opts = {
                        rs.getString("option1"),
                        rs.getString("option2"),
                        rs.getString("option3"),
                        rs.getString("option4")
                };
                int correct = rs.getInt("correct_option");
                list.add(new QuizModel(q, opts, correct));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private void askForNameAndSaveScore() {
        if (timeline != null) timeline.stop();

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Quiz শেষ");
        dialog.setHeaderText("আপনার স্কোর: " + score);
        dialog.setContentText("আপনার নাম লিখুন:");

        dialog.showAndWait().ifPresent(name -> {
            saveScoreToDB(name);
            // UI তে স্কোর দেখাও
            scoreLabel.setText("আপনার স্কোর: " + score + " নম্বর");
            // অপশন এবং বাটন ডিসেবল করো
            disableQuiz();
        });
    }

    private void saveScoreToDB(String name) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO scores (player_id, player_name, score) VALUES (?, ?, ?)"
            );
            ps.setString(1, Main.userId);
            ps.setString(2, name);
            ps.setInt(3, score);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void disableQuiz() {
        opt1.setDisable(true);
        opt2.setDisable(true);
        opt3.setDisable(true);
        opt4.setDisable(true);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("তথ্য");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void startTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLeft--;
            timerLabel.setText("সময় বাকি: " + timeLeft + " সেকেন্ড");
            if (timeLeft <= 0) {
                handleNext();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }
}
