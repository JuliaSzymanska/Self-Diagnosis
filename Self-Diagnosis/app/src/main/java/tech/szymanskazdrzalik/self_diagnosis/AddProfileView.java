package tech.szymanskazdrzalik.self_diagnosis;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import androidx.fragment.app.Fragment;


public class AddProfileView extends Fragment {
    Button button;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1000;
    // TODO: 02.11.2020 - koniecznie przed prezentacją
    //  Menu po cofnięciu się w czacie z botem - Zmiana użytkownika, dodanie nowego użytkownika, historia leczenia użytkownika
    //  Dodać pokazowe przyciski do odpowiedzi do bota (nie powiązane z api, na rzecz prezentacji)

    // TODO: 02.11.2020 Mieszane odczucia co do kiedy
    //  Pierwsze uruchomienie aplikacj - utworzenie uzytkownika ewentualnie pokaz możliwości aplikacji

    // TODO: 02.11.2020 - raczej po prezentacji
    //  Baza danych - dodać tabelę z czatami, powiązane z id użytkownika
    //  Baza danych - zapisywać rozmowę - diagnoza, zapisujemy jednynie ukonczone diagnozy
    //  Interakcja z api
    //  Dodawanie zdj profilowego (dodać do bazy danych)

    private void addProfileImageListener(View v) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, IMAGE_PICK_CODE);
    }


}
