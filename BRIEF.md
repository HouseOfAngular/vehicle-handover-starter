# Zadanie rekrutacyjne: dokumenty pojazdów w salonie

## Kontekst

Salon samochodowy chce przestać sprawdzać dokumenty aut przed wydaniem na papierowej checkliście. Poniżej jest transkrypcja spotkania z salonem. Wymagane wersje JDK i Node są w README. W repo znajdziesz działającą aplikację (Spring Boot + Angular) z gotowym modułem pojazdów. Ten moduł pokazuje, jak w tym projekcie pisze się kod.

Masz **2 godziny**. Pracuj z AI tak, jak pracujesz na co dzień. Narzędzie wybierasz sam.

## Co masz dostarczyć

1. **`INTENT.md`**: wymagania wyciągnięte z transkrypcji, pytania do klienta, przyjęte założenia i rzeczy poza zakresem. Nikt nie odpowie na Twoje pytania w trakcie, więc przy wątpliwościach zapisz założenie i działaj dalej.
2. **Kontekst dla agenta**: plik z konwencjami projektu dla Twojego narzędzia (np. `CLAUDE.md`).
3. **Skill `feature-slice`** w formacie Agent Skills, w katalogu `.claude/skills/feature-slice/` (plik `SKILL.md` i opcjonalne pliki pomocnicze). Skill na podstawie opisu encji generuje kompletny wycinek: backend i frontend zgodny z modułem pojazdów, razem z testami. **Po oddaniu uruchomimy Twój skill na encji, której nie znasz, i porównamy wynik z wynikiem agenta bez Twojego skilla. Liczy się to, co Twój skill poprawia.**
4. **Feature B: klienci.** Wygeneruj go wyłącznie skillem, zanim zaczniesz feature A. To pierwszy test Twojego skilla: jeśli wynik wymaga poprawek, popraw skill i wygeneruj ponownie. Ręczne poprawki kodu opisz w `DECISIONS.md`.
5. **Feature A: dokumenty pojazdu i gotowość do wydania.** Użyj skilla do części zgodnych ze wzorcem modułu pojazdów (encja, migracja, szkielet API i UI). Reguły biznesowe, endpoint gotowości i to, czego wzorzec nie pokrywa, dopisz sam i przetestuj. W `DECISIONS.md` opisz, co wygenerował skill, a co zrobiłeś ręcznie.
6. **`DECISIONS.md`**: gdzie AI się pomyliło, jak to wyłapałeś, co zrobiłeś ręcznie i dlaczego.

## Kontrakt API, którego musisz się trzymać

Reszta API ma być zgodna z konwencjami modułu pojazdów. Poniższe nazwy i ścieżki są stałe, bo uruchamiamy na nich własne testy.

Typy dokumentów (`type`): `SALES_AGREEMENT`, `INVOICE`, `COC`, `OC_POLICY`, `REGISTRATION_POA`.

Statusy dokumentu (`status`): `PENDING`, `VERIFIED`, `REJECTED`.

Dodanie dokumentu typu, który ma już aktywny (nieodrzucony) dokument, zwraca 409. Niedozwolona zmiana statusu dokumentu również zwraca 409.

| Metoda | Ścieżka | Opis |
| --- | --- | --- |
| GET | `/api/vehicles/{vehicleId}/documents` | Lista dokumentów pojazdu |
| POST | `/api/vehicles/{vehicleId}/documents` | Dodanie dokumentu: `type`, `number`, `issueDate`, opcjonalnie `expiryDate`. Nowy dokument ma status `PENDING` |
| PUT | `/api/vehicles/{vehicleId}/documents/{documentId}/status` | Zmiana statusu: `{"status": "VERIFIED"}` |
| GET | `/api/vehicles/{vehicleId}/handover-readiness` | Gotowość pojazdu do wydania |
| CRUD | `/api/customers` | Klienci, według konwencji modułu pojazdów |

Odpowiedź `handover-readiness`:

```json
{
  "ready": false,
  "missingDocumentTypes": ["COC"],
  "problems": [
    { "documentType": "OC_POLICY", "reason": "EXPIRED" },
    { "documentType": null, "reason": "NO_HANDOVER_DATE" }
  ]
}
```

Możliwe wartości `reason`: `NOT_VERIFIED`, `EXPIRED`, `NO_HANDOVER_DATE`.

## Jak oddać pracę

- Repozytorium z historią git. Commituj często, nie jednym commitem na koniec.
- Pełne logi sesji z AI, bez wycinania. Dla Claude Code to pliki z `~/.claude/projects/` dla tego repo; dla innych narzędzi eksport rozmowy.
- Po oddaniu umówimy 30 minut rozmowy, na której pokażesz swój setup na ekranie i wprowadzisz jedną zmianę na żywo.

Oceniamy analizę wymagań, kontekst dla agenta, działanie skilla na nowej encji, poprawność reguł biznesowych i Twoje decyzje. Nie oceniamy liczby promptów ani wyglądu UI ponad poziom modułu pojazdów.

---

# Transkrypcja spotkania

Spotkanie z salonem Auto Mazur, uczestnicy: Kasia (PM po stronie software house'u), Anna (kierowniczka administracji sprzedaży w salonie), Tomek (doradca handlowy). Salon jest fikcyjny.

**Kasia:** Dzień dobry. Zacznijmy od tego, co najbardziej boli przy wydawaniu aut.

**Anna:** Papierologia. Każde auto przed wydaniem musi mieć komplet dokumentów, a dziś sprawdzamy to na wydrukowanej checkliście. W zeszłym miesiącu klient przyjechał po auto i okazało się, że nie ma COC, bo ktoś przeoczył.

**Kasia:** Co wchodzi w komplet?

**Anna:** Umowa sprzedaży, faktura, świadectwo zgodności, czyli COC, i polisa OC. Bez tych czterech auto nie wyjeżdża. Do tego pełnomocnictwo do rejestracji, ale ono jest potrzebne tylko wtedy, kiedy to my rejestrujemy auto za klienta. Część klientów rejestruje sama.

**Kasia:** A jak dziś wiecie, kto rejestruje?

**Anna:** Tomek to wie, bo ustala to z klientem.

**Tomek:** Mam to w mailach. Czasem w notatce na umowie.

**Kasia:** Czyli system musiałby to skądś wiedzieć.

**Anna:** Tomek może to zaznaczać przy aucie, jak ustali z klientem. Domyślnie klient rejestruje sam, więc pełnomocnictwo jest wymagane tylko przy autach, gdzie Tomek to zaznaczy.

**Kasia:** Dokumenty mają jakiś status?

**Anna:** Tak. Jak ktoś wgra skan, dokument czeka na weryfikację. Ja albo Tomek sprawdzamy i jest albo zweryfikowany, albo odrzucony, na przykład jak na fakturze jest zły VIN.

**Tomek:** I jak odrzucimy, a klient doniesie poprawioną wersję, to po prostu przestawiamy status na zweryfikowany, żeby nie robić bałaganu z nowymi wpisami.

**Kasia:** Czy każdy dokument ma datę ważności?

**Anna:** Nie każdy. Umowa i faktura nie mają. Polisa ma, pełnomocnictwo zwykle też. Dokument musi być ważny w dniu wydania auta i ten dzień się jeszcze liczy. Jak polisa kończy się w dniu wydania, to jest w porządku.

**Kasia:** A data wystawienia?

**Anna:** Nie może być z przyszłości. To by znaczyło, że ktoś się pomylił przy wpisywaniu.

**Kasia:** Na jedno auto może być kilka dokumentów tego samego typu?

**Anna:** Jeden aktualny każdego typu. Odrzucone mogą wisieć w historii, ale aktywny tylko jeden. Odrzucony to tak, jakby dokumentu nie było, trzeba donieść nowy.

**Tomek:** Fajnie by było, żebym w aucie od razu widział: gotowe do wydania albo czego brakuje.

**Anna:** Tak. I jak nie ma ustalonej daty wydania, to nie może pokazywać, że gotowe.

**Tomek:** I żeby klient dostawał SMS, jak auto jest gotowe.

**Kasia:** SMS-y to osobna integracja z bramką SMS, więc zostawiamy je na kolejny etap. Coś jeszcze?

**Anna:** Klienci. Chcemy prostą listę klientów: imię, nazwisko, e-mail, telefon, a dla firm NIP. E-mail nie może się powtarzać. Na razie bez powiązań z autami, to później.

**Anna:** Jeszcze jedno, Tomek, bo wcześniej to pominęłam. Odrzucony dokument zostaje odrzucony. Jak klient przyniesie poprawkę, wgrywamy nowy dokument. Inaczej przy kontroli nie widać, co było źle.

**Tomek:** Ale zawsze robiliśmy tak, że przestawiamy status. Inaczej mamy po trzy faktury przy jednym aucie.

**Anna:** Dogadam to jeszcze z dyrektorem. Na razie róbcie po mojemu: odrzucony zostaje odrzucony, a poprawka to nowy dokument. Jak dyrektor zdecyduje inaczej, dam znać.

**Kasia:** Dobrze, dziękuję. Odezwę się z propozycją.
