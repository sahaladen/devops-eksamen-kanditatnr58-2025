for å kunne kjøre dette så må du mest sannsynlig endre branch i github actions fila til main(default branch for deg).
for et eller annet grunn så laget repo-et mitt en master branch og alt er lagret inni der.

lenke for vellykket workflow fil:
https://github.com/sahaladen/devops-eksamen-kanditatnr58-2025/actions/runs/19484119996/job/55762391865 

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

