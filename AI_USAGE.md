# AI usage

## Which AI tools I used and for what

I used **Claude** (claude.ai) for:
- understanding the SIS3 requirements and making a step-by-step plan;
- generating the Compose code step by step: theme, data class, reusable components, the three screens and navigation (I typed it into Android Studio, ran it and fixed the errors);
- explaining Git problems (wrong repository, author identity, rejected push);
- drafting the README structure and this file (I checked them against the app and edited them).

I also used Android Studio autocomplete and the `@Preview` panel to check my screens.

## My 3 most useful prompts

1. > let me send you the movie folder too, so you understand and have all the materials.
2. > Explain step by step how a habit id travels from tapping a HabitCard on the Home screen to the Detail screen, quoting the exact lines from my MainActivity.kt.
3. > Review my QuickHabit project against the SIS3 checklist: Scaffold + TopAppBar on every screen, no hardcoded colors or font sizes in screens, 48 dp touch targets, ellipsis for long text, an empty state, and previews including a dark one. Point out anything I could lose points for at the defense and explain why. Do not rewrite my code.


## One case where the AI was wrong

Claude's first version of the Home screen did not compile. Android Studio showed four "Unresolved reference" errors in the Problems panel and all previews said "Render problem". The causes were two things the code depended on but I did not have yet: the `material-icons-core` dependency (for the `Icons` used in the settings button) and the vector file `ic_empty_habits.xml` for the empty-state image. I noticed it from the red errors and the broken previews, sent a screenshot of the Problems panel, then added the dependency in `app/build.gradle.kts`, created the drawable file and rebuilt. After that the previews rendered correctly.

The lesson for me: AI code can look complete but depend on files and libraries that are missing in my project, so I need to build and read the errors instead of trusting it.

## What I wrote or changed by hand

- All sketches in `design/` (drawn on paper and photographed).
- The Git setup, commits, merge with the remote README and the push.
- I typed in, built and ran all the code, fixed the build errors, and tested the app on my phone.
- I took all screenshots and put them in the README.

