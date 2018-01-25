package com.app.fiskas.fiskas;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bunk3r.spanez.SpanEZ;
import com.bunk3r.spanez.locators.Word;

import static com.bunk3r.spanez.api.EZ.BOLD;
import static com.bunk3r.spanez.api.EZ.UNDERLINE;

public class PrivatePolicy extends AppCompatActivity {

    private String policy_text = "I. Przetwarzanie danych osobowych użytkowników aplikacji mobilnych \n" +
            "   1. Dane dotyczące użytkowników aplikacji mobilnej przetwarzane są przez Fiskas Sp. z o.o., wpisaną do rejestru przedsiębiorców prowadzonego przez Sąd Rejonowy dla m.st. Wrocławia – Fabrycznej, VI Wydział Gospodarczy Krajowego Rejestru Sądowego pod numerem 0000599990, NIP 8943072632, REGON 363655234, ul. Legnicka 150/2, Wrocław będącą w odniesieniu do danych osobowych użytkowników administratorem danych osobowych w rozumieniu ustawy z dnia 29 sierpnia 1997 r. o ochronie danych osobowych (dalej zwaną \"Fiskas\"). \n2. Zbierane przez Fiskas dane osobowe użytkowników przetwarzamy w sposób zgodny z zakresem udzielonego przez użytkownika zezwolenia lub na podstawie innych ustawowych przesłanek legalizujących przetwarzanie danych (przede wszystkim w celu realizacji usług oraz w prawnie usprawiedliwionym celu Fiskas marketingu bezpośrednim naszych produktów lub usług), zgodnie z wymogami prawa polskiego, w szczególności w zgodzie z ustawą z dnia 29 sierpnia 1997 r. o ochronie danych osobowych. Jako administrator danych osobowych użytkowników aplikacji mobilnych możemy w drodze umowy powierzać przetwarzanie tych danych innym podmiotom, w tym zagranicznym, w trybie art. 31 ustawy z dnia 29 sierpnia 1997 r. o ochronie danych osobowych. \n3. Każdemu użytkownikowi, który wypełnił formularz rejestracyjny czy w inny sposób udostępnił nam swoje dane osobowe zapewniamy dostęp do dotyczących go danych w celu ich weryfikacji, modyfikacji lub też usunięcia. Podawanie danych osobowych jest dobrowolne. \n4. Fiskas nie przekazuje, nie sprzedaje i nie użycza zgromadzonych danych osobowych użytkowników innym osobom lub instytucjom, chyba że dzieje się to za wyraźną zgodą lub na życzenie użytkownika, lub też na żądanie uprawnionych na podstawie prawa organów państwa na potrzeby prowadzonych przez nie postępowań. \nII. Polityka w zakresie przechowywania informacji w urządzeniu końcowym użytkownika i uzyskiwania dostępu do informacji tam przechowywanych \n" +
            "1. W związku z udostępnianiem aplikacji mobilnych Fiskas przechowuje informacje w urządzeniu końcowym użytkownika i uzyskuje dostęp do informacji tam przechowywanych. \n2. Działania, o których mowa w punkcie 1 wykonywane są w celu: a) optymalizacji korzystania z aplikacji mobilnych, b) tworzenia statystyk, które pomagają zrozumieć, w jaki sposób użytkownicy korzystają z aplikacji mobilnych, co umożliwia ulepszanie ich struktury i zawartości, c) tworzenia statystyk w celu ich prezentowania, w tym udostępniania partnerom Fiskas. \n3. Ze względu na specyfikę stosowanych przez Fiskas technologii aplikacji mobilnych, użytkownik nie ma możliwości wyłączenia opcji przyjmowania technologii pozwalających na przechowywanie \n" +
            "informacji w urządzeniu końcowym użytkownika i uzyskiwania do nich dostępu. Użytkownicy, którzy nie chcą zezwalać na przechowywanie przez Fiskas informacji w urządzeniu końcowym \n" +
            "użytkownika lub uzyskiwanie przez Fiskas dostępu do tych informacji, nie powinni instalować aplikację mobilną i-TAX Księgowość Fiskas. \n" +
            "III. Informacje zawarte w logach dostępowych \n" +
            "\n" +
            "Podobnie jak większość wydawców aplikacji mobilnych, zbieramy informacje dotyczące korzystania z aplikacji mobilnych przez użytkowników oraz ich adresów IP na podstawie analizy logów dostępowych. Informacje te wykorzystywane są w celach technicznych, związanych z administracją serwerów naszych aplikacji mobilnych, jak również w celach statystycznych, przy analizie demograficznej użytkowników. Stosownie do zapisu art. 18 ust. 6 ustawy z dnia 18 lipca 2002 r. o świadczeniu usług drogą elektroniczną oraz innych obowiązujących przepisów prawa, możemy zostać zobowiązani do wydania informacji, w tym w szczególności numeru IP komputera, zawartych w logach dostępowych na żądanie uprawnionych na podstawie prawa organów państwa na potrzeby prowadzonych przez nie postępowań. \n" +
            "IV. Kontakt  \n" +
            "W przypadku pytań użytkowników dotyczących stosowanej przez Fiskas Polityki Prywatności, jak \n" +
            "również w celu realizacji uprawnień określonych w punkcie I.3. prosimy o kontakt na adres wskazany w punkcie I.1. lub pocztą elektroniczną na adres: kancelaria.fiskas@gmail.com";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_policy);

        TextView policy_txt = (TextView)findViewById(R.id.txt_privacy);
        SpanEZ.from(policy_txt)                                         // 1. pass the target TextView
                .withContent(policy_text)                    // 2. pass the content
                .style(Word.findAll("IV. Kontakt"), BOLD | UNDERLINE)
                .absoluteSize(Word.findAll("IV. Kontakt"), 40)                    // 2. pass the content
                .style(Word.findAll("III. Informacje zawarte w logach dostępowych"), BOLD | UNDERLINE)
                .absoluteSize(Word.findAll("III. Informacje zawarte w logach dostępowych"), 40)
                .style(Word.findAll("II. Polityka w zakresie przechowywania informacji w urządzeniu końcowym użytkownika i uzyskiwania dostępu do informacji tam przechowywanych"), BOLD | UNDERLINE)
                .absoluteSize(Word.findAll("II. Polityka w zakresie przechowywania informacji w urządzeniu końcowym użytkownika i uzyskiwania dostępu do informacji tam przechowywanych"), 40)
                .style(Word.findAll("I. Przetwarzanie danych osobowych użytkowników aplikacji mobilnych"), BOLD | UNDERLINE)
                .absoluteSize(Word.findAll("I. Przetwarzanie danych osobowych użytkowników aplikacji mobilnych"), 40)
                .apply();
        findViewById(R.id.btn_close_policy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
