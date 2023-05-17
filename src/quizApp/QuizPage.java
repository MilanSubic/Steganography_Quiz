package quizApp;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.*;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

import java.util.List;
import java.util.ResourceBundle;

public class QuizPage extends StartPage implements Initializable {



    public Button nextButton;
    public TextArea textArea;
    public TextField answerField;
    public ChoiceBox choiceBox;
    public Label resultLabel;
    public Button resultButton;
    public Label usernameLabel;
    public Button startMenuButton;


    int questionNumber=1;
    int value;
    int result=0;
    List<Integer> questionList=new ArrayList<>();
    String answer;
    boolean onlyOneTime=true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        usernameLabel.setText(username);
        getQuestion();


    }





        public void getQuestion(){

            textArea.setEditable(true);
            String pitanje="";
            Steganography st = new Steganography();
            Random random=new Random();
            value= random.nextInt(20)+1;
            while(questionList.contains(value))
                value= random.nextInt(20)+1;
            questionList.add(value);
            try {
                pitanje=st.reveal("codedslika" + value + ".png");
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            textArea.setText(pitanje);
            questionNumber++;
            setAnswer();
            textArea.setEditable(false);





        }

    public void nextQuestion(ActionEvent actionEvent) {

            checkAnswer();
            if(questionNumber<=5)
            {
                getQuestion();
            }
            else {

                textArea.setText("Kraj kviza! Vaš rezultat je upisan u bazu korisnika.");
                nextButton.setDisable(true);
                choiceBox.setDisable(true);
            }

            resultLabel.setText(String.valueOf(result));


    }

    public void setAnswer(){
            switch(value){

                case 1:
                    answerField.setDisable(true);
                    choiceBox.setDisable(false);
                    choiceBox.getItems().clear();
                    choiceBox.getItems().add("Raža");
                    choiceBox.getItems().add("Kit-ajkula");
                    choiceBox.getItems().add("Morski pas");
                    choiceBox.getItems().add("Delfin");
                    break;
                case 2:
                    answerField.setDisable(true);
                    choiceBox.setDisable(false);
                    choiceBox.getItems().clear();
                    choiceBox.getItems().add("Nulta");
                    choiceBox.getItems().add("A");
                    choiceBox.getItems().add("AB");
                    choiceBox.getItems().add("B");
                    break;

                case 3:
                    answerField.setDisable(true);
                    choiceBox.setDisable(false);
                    choiceBox.getItems().clear();
                    choiceBox.getItems().add("Kripton");
                    choiceBox.getItems().add("Jupiter");
                    choiceBox.getItems().add("Zemlja");
                    choiceBox.getItems().add("Mars");
                    break;

                case 4:
                    answerField.setDisable(true);
                    choiceBox.setDisable(false);
                    choiceBox.getItems().clear();
                    choiceBox.getItems().add("Njemačka");
                    choiceBox.getItems().add("Brazil");
                    choiceBox.getItems().add("Urugvaj");
                    choiceBox.getItems().add("Francuska");
                    break;
                case 5:
                    answerField.setDisable(true);
                    choiceBox.setDisable(false);
                    choiceBox.getItems().clear();
                    choiceBox.getItems().add("35");
                    choiceBox.getItems().add("30");
                    choiceBox.getItems().add("25");
                    choiceBox.getItems().add("37");
                    break;
                case 6:
                    answerField.setDisable(true);
                    choiceBox.setDisable(false);
                    choiceBox.getItems().clear();
                    choiceBox.getItems().add("OUTATIME");
                    choiceBox.getItems().add("MF1985");
                    choiceBox.getItems().add("DOCBROWN");
                    choiceBox.getItems().add("DOC1985");
                    break;
                case 7:
                    answerField.setDisable(true);
                    choiceBox.setDisable(false);
                    choiceBox.getItems().clear();
                    choiceBox.getItems().add("351");
                    choiceBox.getItems().add("206");
                    choiceBox.getItems().add("402");
                    choiceBox.getItems().add("120");
                    break;
                case 8:
                    answerField.setDisable(true);
                    choiceBox.setDisable(false);
                    choiceBox.getItems().clear();
                    choiceBox.getItems().add("5%");
                    choiceBox.getItems().add("10%");
                    choiceBox.getItems().add("15%");
                    choiceBox.getItems().add("25%");
                    break;
                case 9:
                    answerField.setDisable(true);
                    choiceBox.setDisable(false);
                    choiceBox.getItems().clear();
                    choiceBox.getItems().add("Rusija");
                    choiceBox.getItems().add("Indija");
                    choiceBox.getItems().add("Kina");
                    choiceBox.getItems().add("Japan");
                    break;
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                    choiceBox.setDisable(true);
                    answerField.setDisable(false);
                    answerField.setText("");
                    break;


            }

    }

    public void checkAnswer() throws NullPointerException{

        try{
        switch(value){

            case 1:
                if(choiceBox.getSelectionModel().getSelectedItem().equals("Kit-ajkula"))
                    result++;

                break;
            case 2:
                if(choiceBox.getSelectionModel().getSelectedItem().equals("Nulta"))
                    result++;

                break;

            case 3:
                if(choiceBox.getSelectionModel().getSelectedItem().equals("Kripton"))
                    result++;

                break;

            case 4:
                if(choiceBox.getSelectionModel().getSelectedItem().equals("Urugvaj"))
                    result++;

                break;
            case 5:
                if(choiceBox.getSelectionModel().getSelectedItem().equals("30"))
                    result++;
                break;
            case 6:
                if(choiceBox.getSelectionModel().getSelectedItem().equals("OUTATIME"))
                    result++;

                break;
            case 7:
                if(choiceBox.getSelectionModel().getSelectedItem().equals("206"))
                    result++;

                break;
            case 8:
                if(choiceBox.getSelectionModel().getSelectedItem().equals("10%"))
                    result++;

                break;
            case 9:
                if(choiceBox.getSelectionModel().getSelectedItem().equals("Kina"))
                    result++;

                break;
            case 10:


                    answer=answerField.getText();
                    if(answer.equals("Samsung"))
                        result++;
                break;
            case 11:

                    answer=answerField.getText();
                    if(answer.equals("Ag"))
                        result++;
                break;
            case 12:

                    answer=answerField.getText();
                    if(answer.equals("3"))
                        result++;
                break;
            case 13:

                    answer=answerField.getText();
                    if(answer.equals("Egipta"))
                        result++;
                break;
            case 14:

                    answer=answerField.getText();
                    if(answer.equals("dijamant"))
                        result++;
                break;
            case 15:

                    answer=answerField.getText();
                    if(answer.equals("World Wide Web"))
                        result++;
                break;
            case 16:

                    answer=answerField.getText();
                    if(answer.equals("jagoda"))
                        result++;
                break;
            case 17:

                    answer=answerField.getText();
                    if(answer.equals("Coca Cola"))
                        result++;
                break;
            case 18:

                    answer=answerField.getText();
                    if(answer.equals("Francuska"))
                        result++;
                break;
            case 19:

                    answer=answerField.getText();
                    if(answer.equals("Holandija"))
                        result++;
                break;
            case 20:

                    answer=answerField.getText();
                    if(answer.equals("1. svjetski rat"))
                        result++;
                break;

        }
        }catch(NullPointerException exception) {
            System.out.println("Prazan odgovor!");}
    }

    public void resultAction(ActionEvent actionEvent) throws IOException {

                if(onlyOneTime) {
                    textArea.setEditable(true);
                    decryptCall();
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    textArea.appendText(username + "     " + dtf.format(now) + "     " + result + "\n");
                    encryptCall();
                     textArea.setEditable(false);
                     onlyOneTime=false;
                }
                else {
                    Alert alert=new Alert(Alert.AlertType.WARNING);
                    alert.setContentText("Vaš rezultat je već upisan!");
                    alert.show();
                }


    }

    public void decryptCall() throws IOException {
        Encryption encryption=new Encryption();
        byte[] cryptoData;
        Path decryptPath= Paths.get("C:\\Users\\HP\\Desktop\\Kripto\\result.txt");
        cryptoData=Files.readAllBytes(decryptPath);
        String key="dessifra";
        byte[] dec =encryption.Decrypt(key,cryptoData);
        String text= new String(dec);
        textArea.setText(text);
    }

    public void encryptCall() throws IOException {
        Encryption encryption=new Encryption();
        byte[] fileData;
        fileData=textArea.getText().getBytes();
        String key="dessifra";
        byte[] dec =encryption.Encrypt(key,fileData);
        FileOutputStream cryptoDataOut = new FileOutputStream("result.txt");
        cryptoDataOut.write(dec);
        cryptoDataOut.close();


    }

    public void backOnStart(ActionEvent actionEvent) throws IOException {

        Parent secondPage= FXMLLoader.load(getClass().getResource("StartPage.fxml"));
        Stage window= (Stage) startMenuButton.getScene().getWindow();
        window.setTitle("QuizApp");
        window.setScene(new Scene(secondPage, 600, 600));
    }
}
