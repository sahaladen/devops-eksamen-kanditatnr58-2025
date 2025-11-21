oppgave 1:
valgte å lage bucketen manuelt i S3
også importere den i github actions slik at hver gang endringer skjer så er det bare å pushe til github


oppgave 2:

lenke til workflow filen: https://github.com/sahaladen/devops-eksamen-kanditatnr58-2025/blob/master/.github/workflows/sam-deploy.yml
lenke til vellykket deployment: https://github.com/sahaladen/devops-eksamen-kanditatnr58-2025/actions/runs/19198938597/job/54883973101
lenke til vellykket pull request: https://github.com/sahaladen/devops-eksamen-kanditatnr58-2025/actions/runs/19199556050/job/54885416620?pr=1

hvis du skal kjøre dette på din egen konto så kan det hende at du må endre hvor github actions pusher.
for meg så var det master men du kan endre dette enkelt til main eller en annen branch hvis du vil.
husk å legge til aws key og secret key i din github konto.
hvis du kjører koden lokalt, bare husk å ha docker på.



oppgave 3:


for å kunne kjøre dette så må du mest sannsynlig endre branch i github actions fila til main(default branch for deg).
for et eller annet grunn så laget repo-et mitt en master branch og alt er lagret inni der.

lenke for vellykket workflow fil:
https://github.com/sahaladen/devops-eksamen-kanditatnr58-2025/actions/runs/19484119996/job/55762391865

navn på container image i docker hub: somaler/sentiment-docker

Jeg brukte docker/metadata-action som automatisk genererer flere tags basert på regler:
Dette gir deg 3 tags automatisk:

type=ref,event=branch → master

Taggen basert på branch-navnet


type=sha,prefix={{branch}}- → main-a1b2c3d

Branch + short commit SHA (7 tegn)
Unikt for hver commit


type=raw,value=latest → latest

Kun når det er master branch (default branch)

Fordeler med denne strategien:

Sporing: Du kan alltid finne hvilken commit som produserte et image
Rollback: Enkelt å rulle tilbake til main-abc1234 hvis latest feiler
Automatisk: Ingen manuell versjonering nødvendig
Fleksibelt: Lett å kjøre både latest og spesifikke commits






oppgave 4:


Metrikker og designvalg

Counter (sentiment.analysis.total): Teller totalt antall analyser utført per selskap og sentiment. Brukes for løpende operasjoner der verdien alltid øker, som registrering av nye analyser via recordAnalysis().

Gauge (sentiment.analysis.companies.detected): Viser antall selskaper som ble funnet i siste analyse. Kan øke og synke, og oppdateres dynamisk via recordCompaniesDetected().
En annen Gauge er sentiment.analysis.latest.confidence, som alltid viser siste konfidensscore ved hjelp av AtomicReference og recordConfidence().

Timer (sentiment.analysis.duration): Måler hvor lang tid hver sentimentanalyse tar, og registrerer varighet per selskap og modell. Gir gjennomsnitt, maks og persentiler via recordDuration().

DistributionSummary (sentiment.analysis.confidence): Registrerer alle konfidensscores per selskap. Gir statistisk fordeling over analysene, og oppdateres hver gang recordConfidence() kalles.

LongTaskTimer (sentiment.analysis.bedrock.longtask): Måler antall langvarige Bedrock-anrop som kjører akkurat nå. Starter og stoppes via startBedrockCall() og stopBedrockCall().

Designvalg:

Instrumenttypen er valgt ut fra hvordan verdien oppfører seg og hva som skal måles: Counter for løpende akkumulering,
Gauge for dynamiske verdier som kan synke og øke, Timer for måling av varighet,
DistributionSummary for statistikk over konfidensscores, og LongTaskTimer for aktive langvarige prosesser. Dette reflekteres direkte i implementasjonen med Micrometer og Spring Boot.


bilde:
![CloudWatch Metrics Screenshot](/sentiment-docker/img.png)
![CloudWatch Metrics Screenshot for terrafrom koden til del B](/sentiment-docker/img_1.png)
![fikk lagt inn SNS men hadde problemer med å få trigge alarmen](/sentiment-docker/img_2.png)







Oppgave 5:

en drøftelse på hvordan KI-assistert programvareutvikling påvirker de tre grunnleggende DevOps-prinsippene. Den vil bli delt opp på 3 deler der jeg går gjennom fordeler og ulemper med dette.

Flyt:
En fordel med KI-assistert programvareutvikling innenfor «fly» prinsipp er f.eks å automatisere tester.  I følge denne artikkelen (kilde: https://www.rainforestqa.com/blog/ai-testing-tools) så bruker over 80% av firmaer som var med på denne undersøkelsen KI til å oppdatere og vedlikeholde tester. Grunnen til at jeg mener at dette er en fordel er at utviklere ikke trenger å bruke tid på skrive tester sånn at integrasjon kan skje kjappere. For da kan vi gå fra Unit test over til integrasjon og ny funksjonalitet eller produktet kommer ut raksere.. En ulempe med dette kan være at KI hallusinerer eller skriver f.eks en test hvor det kan være ingen kode eller bare en metode som returner NULL for å få testen til å bli grønn. Jeg har opplevd selv at KI hallusinerer og gir meg en variabel som ikke er deklarert.. KI er et verktøy og ikke en erstatning for arbeid.

Feedback:
Å bruke KI til feedback har store fordeler. En av dem er f.eks å overvåke hva slags ressurser som blir brukt av infrastrukturen og få real time alarmer på dette. Ved å bruke KI til dette så vil man raskt finne «sløsing» av ressurser og ta raskt grep på dette. Ulempen med dette kommer fra at siden det er KI så kan det være vanskelig å håndtere falske positiver siden det er KI som håndterer dette og ikke kode generert av mennesker så blir det vanskelig å legge inn regler for situasjoner som krever abstrakt tenking. Siden KI er ikke determenistisk så er dette et stort problem.

Kontinuerlig læring og forbedring:
Dette er en devops prinisipp som KI kan virkelig kinne. Som jeg nevnte sist så er KI ikke determenistisk som gjør det vanskelig å få genert f.eks stabil og brukbar kode. Men det finnes måter å få dette til på og det er mange dokumenter som har blitt laget der man kan få KI agenter til å følge regler som gjør dem deterministisk. Dette vil si at KI-en kan lære av feilene sine og bli mye bedre uten å gjøre en stor tabbe som f.eks å ta vekk en hel database. Et problem med dette er at man trenger enten en stor teknisk kunnskap bak hvordan KI fungerer og feltet du er innafor for å kunne lage disse dokumentene der den er tilpasset til kodebasen din. Dokumentet kan også bli veldig stor for å få dette til. Et annet problem av bruk av KI for læring kan være at utviklere ikke lærer lenger eller bruker ikke deler av hjernen sin man pleide å bruke før overdreven bruk av KI.  Basert på denne rapporten om hva MIT studie fant på overdrevet bruk av KI (kilde: https://www.nextgov.com/artificial-intelligence/2025/07/new-mit-study-suggests-too-much-ai-use-could-increase-cognitive-decline/406521/) viser at KI kan gjøre man har lavere minneoppbevaring, svakere hjernetilkobling og svakere eierskap til arbeidet sitt. Kommer fra personlig erfaring så opplever jeg selv at for mye bruk av KI gjør meg mindre stolt av kode jeg skriver.
Konklusjon:
KI er et verktøy som går ingen steder den har transformert hele IT industrien på både gode og dårlige måter. Jeg personlig er veldig spent på hva KI kan gjøre innafor helse med tanke på om man kan få automatisert arbeid som tar unødvendig lang tid f.eks rapportering av pasienter. En av oppgave vi kunne ta for smidig prosjekt var en helse firma hvor de brukte KI for å automatisk fylle inn rapport for ambulanse pasienter slik at når pasienten kom fram til sykehuset så var all form av rapportering gjort slik ambulanse arbeidere ikke trenge å slipper å sitte og skrive en rapport når de har en pasient i bilen. En personlig bekymring jeg har med KI er ID-tyveri og hvor lett det har blitt med hjelp av KI. Det har aldri vært så enkel å bruke KI til å klone stemmen til noen og bruke det for ond hensikt. Man kan til og med bruke KI for å lage falske bilder og videoer av hvem enn man vil. 
Til slutt er det viktig at vi møter KI med både nysgjerrighet og ansvar, slik at vi kan utnytte teknologiens enorme potensial uten å gå på kompromiss med sikkerhet, tillit og menneskelige verdier.
