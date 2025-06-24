# BoardGame

##  Group Members

| Student Name    | GitHub Username    |
|--------------------|--------------------|
| Zago Alessandro     | @Alessandro1234567   |
| Davoodi Danial     | @Danial-BZ   |
| Chen Nicola     | @cennicola6   |

---

## Usage

1. Clona il repository:
```bash
   git clone git@github.com:Alessandro1234567/BoardProject.git
   ```
2. Run the Gradle wrapper (once is enough):
```bash
   gradle wrapper
   ```
### Starting the application
> on macOS/Linux
```bash
   ./gradlew run
   ```
> on Windows
```bash
   gradlew.bat run
   ```
### With an IDE

Open this repository as a Gradle project.

Then to start the application, run the method [DesktopLauncher.main](https://github.com/Alessandro1234567/BoardProject/blob/main/desktop/src/it/unibz/inf/pp/clash/DesktopLauncher.java) (in your running configuration, you may need to specify `assets` as the Java working directory).

---

## Project description

 Inserisci una descrizione sintetica ma chiara del progetto.  
> Cosa fa, qual è lo scopo, in che contesto è stato realizzato.

---

## User guide

> Scrivi qui una guida rapida su come usare il programma.  
> In alternativa, inserisci il link a un video dimostrativo:

[Guarda il video](https://link-al-video.com)

---

## Implementation overview

### Componenti principali e interfacce

- **Componente A**: descrizione breve
- **Componente B**: descrizione breve
- **Interfaccia tra A e B**: descrizione

### Librerie di terze parti utilizzate

- `libreria1` - spiegazione breve
- `libreria2` - spiegazione breve

### Tecniche di programmazione rilevanti

- Uso di pattern come ...
- Tecniche viste a lezione: ...
- Altre scelte importanti: ...

---

## Human experience

### Work Distribution

- **Alessandro Zago**:  
  I started by creating the `utils` folder, where I implemented the `UnitGenerator` file. This component is responsible for generating units and is used within `RealSnapshot`.  
  Additionally, I developed the `doAttack` method inside `RealEventHandler`, which handles the attack logic in the game.
  
- **Nome 2**: ha curato ...
- **Nome 3**: si è occupato di ...

### Uso di Git

> Descrivi come avete collaborato tramite Git (branch, merge, pull request, ecc.)

### Sfide affrontate (una per membro)

- **Alessandro Zago**:  
  During development, I encountered several hurdles that affected efficiency and required focused analysis:

  - **Project Structure**:  
    Understanding the interdependencies between modules took significant time. I traced folder hierarchies and entry points to identify how to implement feature changes.

  #### Single Board Instance:

  - **Advantage**:  
    Centralized state in `model/impl/RealEventHandler.doAttack()` made coordinating attack sequences straightforward and reduced data duplication.

  - **Disadvantage**:  
    In `model/utils/UnitGenerator`, spawning units safely demanded extra synchronization and collision checks, increasing code complexity.

- **Nome 2**: ha avuto difficoltà con ...

- **Nome 3**: ha imparato a ...

---

## 📎 Altre note (opzionale)

> Inserisci eventuali ringraziamenti, avvertenze o dettagli extra.
