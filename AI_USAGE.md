# AI usage

## Which AI tools I used and for what

I used **Claude** (claude.ai) for:
- understanding the SIS3 and SIS4 requirements and making a step-by-step plan;
- suggesting what to draw and label on the screen sketches (SIS3). I drew and photographed them myself;
- drawing the first architecture diagram for SIS4 from my whiteboard photo and the plan (`design/architecture.png`), and a final version with the real names (`design/architecture_final.png`);
- explaining the code step by step: in SIS3 the theme, components, screens and navigation; in SIS4 the Repository, `HomeUiState`, `HomeViewModel`, `HomeRoute` and the stateless `HomeScreen`. I asked it to tell me where to change the code and to write a comment for every line, and I typed the code into Android Studio myself, ran it and fixed the errors;
- explaining Git problems (wrong repository, author identity, rejected push);
- drafting the README structure and this file (I checked them against the app and edited them).

I also used Android Studio autocomplete and the `@Preview` panel to check my screens.

## My 3 most useful prompts

1. > Будем двигаться по немногу и просто говори места что бы поменяли вот код, потому что я сам буду писать а не просто копи пастить
   (sent with my current `MainActivity.kt`; this made Claude give me small steps with exact places to edit instead of one big file)
2. > можешь писать для каждой строки коментарий для чего toMutableSet напиример и в будущем тож
   (every line of the Repository, UiState and ViewModel came with a comment, so I can explain each of them at the defense)
3. > внутри функий таких как рефреш ничего нету шаг 12
   (I pasted my `HomeViewModel` code; it showed that I had not written `refresh()` yet and that I had a typo `gethabits` in the function name)

## One case where the AI was wrong (wrong layer)

In my SIS3 app, which was written with Claude's help, the filtering was **inside the `HomeScreen` composable**:

```kotlin
var selected by rememberSaveable { mutableStateOf("All") }
val filtered = when (selected) {
    "Done" -> habitList.filter { it.doneToday }
    "Not done" -> habitList.filter { !it.doneToday }
    else -> habitList.toList()
}
```

The same mistake was in `FilterChipRow`, which counted the habits for the chip labels with `habitList.count { ... }`. This is business logic in the View layer. It also kept the habit list in `remember` inside `QuickHabitApp`, so everything was lost on rotation.

How I moved it:
- The filtering became `HomeViewModel.applyFilter(filter)` and is called from `onFilterSelected` and `refresh()`. The selected filter is now `HomeUiState.selectedFilter`.
- The counters moved into `refresh()`: `doneCount` and `totalCount` are calculated in the ViewModel, and `FilterChipRow` only receives these numbers.
- The habit list moved into `HabitRepository`. `HomeScreen` now receives `uiState` and lambdas (`onFilterSelected`, `onToggle`, `onRetry`), and has no `remember` and no `filter` inside.
- I checked it with the rotation test: the selected filter and the habits survive.

Other small mistakes of the AI that I found while working:
- Claude first told me to type the Logcat filter as `HomeViewModel|MainActivity`. It showed "All log entries are hidden by the filter", because `|` in a plain text is not "or". The working filter was `tag~:HomeViewModel|MainActivity`.
- In SIS3, Claude's first version of the Home screen did not compile: the `material-icons-core` dependency and the `ic_empty_habits.xml` drawable were missing. I noticed it from the red errors and the broken previews, and added both.

The lesson for me: AI code can look fine but put logic in the wrong layer or depend on things I do not have. I need to build it, run it, rotate the phone and read the logs instead of trusting it.

## What I wrote or changed by hand

- All sketches in `design/` (drawn on paper and photographed).
- The Git setup, commits and pushes.
- I typed in, built and ran all the code, fixed the build errors (for example the typo in `refresh()` and the filtered list in the Detail screen), and tested the app on an emulator and on my phone.
- I made the Lifecycle and ViewModel logs, did the rotation test, tested the error state with `shouldFail = true`, and took the screenshots for the README.
