package quizApp;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.jce.X509KeyUsage;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.x509.X509V2CRLGenerator;
import org.bouncycastle.x509.X509V3CertificateGenerator;

import java.io.*;
import java.math.BigInteger;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.*;
import java.util.*;


public class StartPage {

    public PasswordField passwordField;
    public TextField usernameField;
    public Button logInButton;
    public Button signInButton;
    public TextArea textArea;
    public static String username;
    public static Integer usernameNumberOfLogIn;
    public Integer clientCounter=1;
    public static HashMap<String,Integer> hashMap= new HashMap<String,Integer>();
    public static X509CRL crl;

    public Boolean passwordCorrect=false;




    public void signInAction(ActionEvent actionEvent) throws Exception {

        try{
            if(usernameField.getText().equals("") || passwordField.getText().equals("")) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Upozorenje");
                alert.setContentText("Unesite podatke da bi ste se registrovali!");
                alert.showAndWait();

            }
            else {

                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(
                        passwordField.getText().getBytes(StandardCharsets.UTF_8));
                String sha256hex = new String(Hex.encode(hash));

                BufferedWriter writer = new BufferedWriter(new FileWriter("hashs.txt", true));
                writer.append("username:" + "   " + usernameField.getText() + "   " + "hash:" + "   " + sha256hex);
                writer.newLine();
                writer.close();

                if (clientCounter % 2==0)
                    createKeyStore(usernameField.getText(),passwordField.getText(),"CA2");
                else
                    createKeyStore(usernameField.getText(),passwordField.getText(),"CA1");
                clientCounter++;

                textArea.setWrapText(true);
                textArea.setText("Registracija je uspješna, korisnički sertifikat je kreiran i nalazi se na lokaciji:\n\n");
                textArea.appendText("C:\\Users\\HP\\Desktop\\Kripto\\clientCerts\\"+usernameField.getText()+".p12");
                username=usernameField.getText();
                usernameField.setText("");
                passwordField.setText("");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public X509CRL exportCrlList(String ca, String password) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, UnrecoverableEntryException, CRLException, InvalidKeyException, SignatureException {

        KeyStore caStore = KeyStore.getInstance("PKCS12");
        caStore.load(new FileInputStream("C:\\Users\\HP\\Desktop\\Kripto\\caCerts\\"+ca+".p12"),
                password.toCharArray());
        X509Certificate cert=(X509Certificate) caStore.getCertificate(ca);
        X509V2CRLGenerator crlGen=new X509V2CRLGenerator();
        crlGen.setIssuerDN(cert.getSubjectX500Principal());
        crlGen.setThisUpdate(Calendar.getInstance().getTime());
        Calendar calendar=new GregorianCalendar();
        calendar.add(Calendar.YEAR,1);
        crlGen.setNextUpdate(calendar.getTime());
        crlGen.setSignatureAlgorithm("SHA1WithRSAEncryption");
        crlGen.addExtension(X509Extensions.CRLNumber,false,new CRLNumber(BigInteger.valueOf(Math.abs(new Random()
                .nextLong()))));
        PrivateKey privateKey=getPrivateKeyFromKeyStore(ca,password);
        crl = crlGen.generate(privateKey,new SecureRandom());


        var tmp=crl.getEncoded();
        OutputStream outputStream=new FileOutputStream(ca+"-crlLista.crl");
        outputStream.write(tmp);
        outputStream.flush();
        outputStream.close();
        return crl;
        }



public static X509Certificate getClientCertificate(String keystoreName,String password) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
    KeyStore caStore = KeyStore.getInstance("PKCS12");
    caStore.load(new FileInputStream("C:\\Users\\HP\\Desktop\\Kripto\\clientCerts\\"+keystoreName+".p12"),
            password.toCharArray());
    X509Certificate certificate=(X509Certificate) caStore.getCertificate(keystoreName);
    return certificate;


}



    public static PrivateKey getPrivateKeyFromKeyStore(String keystoreName,String password) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableEntryException {

        KeyStore caStore = KeyStore.getInstance("PKCS12");
        caStore.load(new FileInputStream("C:\\Users\\HP\\Desktop\\Kripto\\caCerts\\"+keystoreName+".p12"),
                password.toCharArray());
        X509Certificate ca=(X509Certificate) caStore.getCertificate(keystoreName);



        KeyStore.PrivateKeyEntry privateKeyEntry=(KeyStore.PrivateKeyEntry)caStore.getEntry(keystoreName,new KeyStore.PasswordProtection(password.toCharArray()));

        PrivateKey privateKey =privateKeyEntry.getPrivateKey();
        return privateKey;


    }

    public static void createKeyStore(String username,String password,String ca) {
        try {
            String storeName = "C:\\Users\\HP\\Desktop\\Kripto\\clientCerts\\"+username+".p12";
            java.security.KeyPairGenerator keyPairGenerator = KeyPairGenerator
                    .getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();

            PrivateKey privateKey =getPrivateKeyFromKeyStore(ca,"lozinka");



            X509Certificate cert = createCertificate("CN="+username, "CN="+ca,
                    publicKey, privateKey);

            java.security.cert.Certificate[] outChain = { cert };
            KeyStore outStore = KeyStore.getInstance("PKCS12");
            outStore.load(null,password.toCharArray());
            outStore.setKeyEntry(username, privateKey, password.toCharArray(),
                    outChain);
            OutputStream outputStream = new FileOutputStream(storeName);
            outStore.store(outputStream, password.toCharArray());
            outputStream.flush();
            outputStream.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private static X509Certificate createCertificate(String dn, String issuer,
                                                     PublicKey publicKey, PrivateKey privateKey) throws Exception {
        X509V3CertificateGenerator certGenerator = new X509V3CertificateGenerator();
        certGenerator.setSerialNumber(BigInteger.valueOf(Math.abs(new Random()
                .nextLong())));

        certGenerator.setSubjectDN(new X509Name(dn));
        certGenerator.setIssuerDN(new X509Name(issuer));
        certGenerator.setNotBefore(Calendar.getInstance().getTime());
        certGenerator.addExtension(
                new ASN1ObjectIdentifier("2.5.29.19"),
                false,
                new BasicConstraints(false));
        certGenerator.addExtension(
                new ASN1ObjectIdentifier("2.5.29.15"),
                true,
                new X509KeyUsage(
                        X509KeyUsage.digitalSignature |
                                X509KeyUsage.nonRepudiation   |
                                X509KeyUsage.keyEncipherment  |
                                X509KeyUsage.dataEncipherment));

        Calendar calendar=new GregorianCalendar();
        calendar.add(Calendar.YEAR,1);
        certGenerator.setNotAfter(calendar.getTime());
        certGenerator.setPublicKey(publicKey);
        certGenerator.setSignatureAlgorithm("SHA1WithRSAEncryption");
        X509Certificate certificate = certGenerator.generate(
                privateKey, "BC");
        return certificate;
    }

    public void logInAction() throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException, InvalidKeyException, UnrecoverableEntryException, CRLException, SignatureException,NullPointerException {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selektujte svoj sertifikat");
        fileChooser.setInitialDirectory(new File("C:\\Users\\HP\\Desktop\\Kripto\\clientCerts"));

        File file = fileChooser.showOpenDialog(Stage.getWindows().get(0));

        if (file != null) {

            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Certificate log in");
            dialog.setContentText("Unesite lozinku vašeg keystore-a:");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                username = file.getName().substring(0, file.getName().length() - 4);
                BufferedReader bufferedReader = new BufferedReader(new FileReader("hashs.txt"));
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(
                        result.get().getBytes(StandardCharsets.UTF_8));
                String passwordHash = new String(Hex.encode(hash));
                String line = "username:   " + username + "   hash:   " + passwordHash;
                String getLine = "";
                while ((getLine = bufferedReader.readLine()) != null) {
                    if (getLine.equals(line)) {
                        passwordCorrect = true;
                        usernameNumberOfLogIn = hashMap.get(username);
                        if (usernameNumberOfLogIn == null) {
                            usernameNumberOfLogIn = 0;
                        }
                        hashMap.put(username, ++usernameNumberOfLogIn);

                        X509Certificate clientCertificate = getClientCertificate(username, result.get());
                        BigInteger clientCertificateID = clientCertificate.getSerialNumber();
                        String caValue=clientCertificate.getIssuerDN().toString();
                        String caName=caValue.substring(3,6).toLowerCase(Locale.ROOT);
                        File crlFile=new File(caName+"-crlLista.crl");
                        if (!crlFile.exists()) {
                            crl = exportCrlList(caName, "lozinka");

                        }else
                        {
                            CertificateFactory certificateFactory= CertificateFactory.getInstance("X.509");
                            InputStream inputStream =new FileInputStream(caName+"-crlLista.crl");
                            crl=(X509CRL) certificateFactory.generateCRL(inputStream);
                        }

                        X509V2CRLGenerator crlGen = new X509V2CRLGenerator();
                        if (crl.isRevoked(clientCertificate)) {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Upozorenje");
                            alert.setContentText("Vaš sertifikat je povučen!");
                            alert.showAndWait();
                            continue;


                        }


                        else{
                                if (usernameNumberOfLogIn == 3) {
                                    KeyStore caStore = KeyStore.getInstance("PKCS12");
                                    caStore.load(new FileInputStream("C:\\Users\\HP\\Desktop\\Kripto\\caCerts\\" + caName + ".p12"),
                                            "lozinka".toCharArray());
                                    X509Certificate cert = (X509Certificate) caStore.getCertificate(caName);
                                    crlGen.setIssuerDN(cert.getSubjectX500Principal());
                                    crlGen.setThisUpdate(Calendar.getInstance().getTime());
                                    Calendar calendar = new GregorianCalendar();
                                    calendar.add(Calendar.YEAR, 1);
                                    crlGen.setNextUpdate(calendar.getTime());
                                    crlGen.setSignatureAlgorithm("SHA1WithRSAEncryption");
                                    Set<X509CRLEntry> revokedCertificates = (Set<X509CRLEntry>) crl.getRevokedCertificates();
                                    if (revokedCertificates != null) {
                                        for (Iterator<X509CRLEntry> it = revokedCertificates.iterator(); it.hasNext(); ) {
                                            X509CRLEntry next = it.next();
                                            crlGen.addCRLEntry(next.getSerialNumber(), next.getRevocationDate(), 5); // 5 kod za prestanak rada
                                        }
                                    }
                                    crlGen.addCRLEntry(clientCertificateID, Calendar.getInstance().getTime(), 5);
                                    crlGen.addExtension(X509Extensions.CRLNumber, false, new CRLNumber(BigInteger.valueOf(Math.abs(new Random()
                                            .nextLong()))));
                                    PrivateKey privateKey = getPrivateKeyFromKeyStore(caName, "lozinka");
                                    crl = crlGen.generate(privateKey, new SecureRandom());
                                    var tmp = crl.getEncoded();
                                    OutputStream outputStream = new FileOutputStream(caName+"-crlLista.crl");
                                    outputStream.write(tmp);
                                    outputStream.flush();
                                    outputStream.close();


                                    System.out.println("uspjelo");
                                } else {
                                    hashMap.put(username, usernameNumberOfLogIn);
                                    System.out.println("broj logovanja"+usernameNumberOfLogIn);
                                }

                            }



                        Steganography st = new Steganography();
                        for (int i = 1; i <= 20; i++) {
                            //st.hide("pitanje"+i+".txt", "slika"+i+".png");
                            st.reveal("codedslika" + i + ".png");
                        }
                        changeScane();


                        }



                    }
                if(!passwordCorrect) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Upozorenje");
                    alert.setContentText("Pogrešna lozinka keystore-a!");
                    alert.showAndWait();

                }


                }

            }

        }


        public void changeScane () throws IOException {

            Parent secondPage = FXMLLoader.load(getClass().getResource("QuizPage.fxml"));
            Stage window = (Stage) logInButton.getScene().getWindow();
            window.setTitle("QuizApp");
            window.setScene(new Scene(secondPage, 600, 600));


        }


    }