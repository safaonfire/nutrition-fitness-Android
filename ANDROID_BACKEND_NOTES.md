# Android integration notes (Spring Boot backend unchanged)

This Android project targets the supplied Spring Boot API at `http://10.0.2.2:8081/` for an Android emulator.

## Android work for your user stories
- US3: Edit name and profile fields and submit them to `PUT /api/clients/profil`.
- US4: Enter age, weight, height and sex with client-side validation.
- US5: Select one of the objective values currently supported by the backend: `perdre_poids`, `gagne_poids`, `maintenir_masse`.
- US6: Request a password-reset email. Shows a message if the email doesn't exist; the backend sends a reset link.

## Limitations found in the supplied backend (not changed)
1. `ClientController` has no `GET /api/clients/profil`; Android no longer calls that nonexistent endpoint. Login returns only a subset of profile data, so physical fields must be entered on the profile screen.
2. `ClientController.modifierProfil` uppercases the goal before `Client.Objectif.valueOf(...)`, but the supplied enum values are lowercase. Profile updates will therefore fail with `Objectif invalide` even for supported goals. The backend has deliberately not been modified.
3. The enum has three objective values, not the fourth “Improve fitness” requested by US5. Android offers only values accepted by the current enum.

The Android project is adapted to the supplied backend without changing Spring Boot. Full acceptance of US3/US5 is blocked by the existing backend limitations above.
