#! /bin/bash
cd "$(dirname "$0")"/.. || exit 1

# TODO change this to point to your mincamlc executable if it's different, or add
# it to your PATH. Use the appropriate option to run the parser as soon
# as it is implemented
MINCAMLC=java/mincamlc

# run all test cases in syntax/valid and make sure they are parsed without error
# run all test cases in syntax/invalid and make sure the parser returns an error

# TODO extends this script to run test in subdirectories
# 

red='\033[0;31m'
normal='\033[0;0m'
DOSSIER_RESULTAT="resultatsTests"

function assertEquals()
{
    if [ $1 -ne $2 ]
    then
	testReussi=0
	printf "        --> le test $fichierATester ${red}ECHOUE${normal}. Cause : $3\n"
    fi
}

function lancerTest()
{
	fichierATester=$1
	doitCompiler=0
	testReussi=1
	echo "lancement de $fichierATester"
	if [[ "$fichierATester" =~ .*/invalid/.*\.ml ]]
	then
		codeRetourAttendu=1
	else
		codeRetourAttendu=0
	fi
	if [[ "$fichierATester" =~ tests/syntax/.* ]]
	then
		$MINCAMLC "$fichierATester" -p 2>/dev/null 1>/dev/null
		assertEquals $codeRetourAttendu $? "parsing mincaml"
		
	elif [[ "$fichierATester" =~ tests/typechecking/.* ]]
	then
		$MINCAMLC "$fichierATester" -t 2>/dev/null 1>/dev/null
		assertEquals $codeRetourAttendu $? "typechecking mincaml"
	else
		doitCompiler=1
	fi
	if [ $doitCompiler -eq 1 ]
	then		
		fichierSansUnderscore=$(echo $fichierATester | tr "/" "_")
		executableCaml="$DOSSIER_RESULTAT/$fichierSansUnderscore.executable"
		traceCaml="$DOSSIER_RESULTAT/$fichierSansUnderscore.res"
		fichierAsml="$DOSSIER_RESULTAT/$fichierSansUnderscore.asml"
		traceAsml="$fichierAsml.res"
		sourceAssembleur="$DOSSIER_RESULTAT/$fichierSansUnderscore.s"
		objetAssembleur="$sourceAssembleur.o"
		executableAssembleur="$sourceAssembleur.executable"
		traceAssembleur="$sourceAssembleur.res"
		ocamlc "$fichierATester" -o "$executableCaml" 2>/dev/null 1>/dev/null
		assertEquals 0 $? "compilation ocaml"
		"$executableCaml" > "$traceCaml"
		$MINCAMLC "$fichierATester" -asml -o "$fichierAsml" 2>/dev/null 1>/dev/null
		assertEquals $codeRetourAttendu $? "compilation mincaml vers asml"
		tools/asml "$fichierAsml" > "$traceAsml" 2>/dev/null
		diff "$traceCaml" "$traceAsml" 2>/dev/null 1>/dev/null
		assertEquals 0 $? "diff ocaml asml"
		$MINCAMLC "$fichierATester" -o "$sourceAssembleur" 2>/dev/null 1>/dev/null		
		assertEquals 0 $? "compilation mincaml vers assembleur"
		arm-eabi-as -mfpu=vfpv2 -o "$objetAssembleur" "$sourceAssembleur" ARM/libmincaml.S  2>/dev/null 1>/dev/null # l'option -mfpu=vfpv2 est necessaire pour l'increment 6 (nombre flottants)
		assertEquals 0 $? "compilation assembleur"
		arm-eabi-ld -o "$executableAssembleur" "$objetAssembleur" 2>/dev/null 1>/dev/null
		assertEquals 0 $? "edition de lien assembleur"
		qemu-arm "$executableAssembleur" > "$traceAssembleur" 2>/dev/null
		diff "$traceCaml" "$traceAssembleur" 2>/dev/null 1>/dev/null
		assertEquals 0 $? "diff ocaml assembleur"
	fi
	
	if [ "$testReussi" -eq 1 ]
	then
		nbTestsQuiPasse=$(( $nbTestsQuiPasse+1 ))
	fi
	nbTests=$(( $nbTests+1 ))
}

export PATH="/opt/gnu/arm/bin:$PATH"

if [ -d "$DOSSIER_RESULTAT" ]
then
	rm -r "$DOSSIER_RESULTAT"
fi
mkdir "$DOSSIER_RESULTAT"
nbTests=0
nbTestsQuiPasse=0
for test_case in tests/*/*/*.ml
do
    lancerTest "$test_case"
done
rm tests/*/*/*.cmi
rm tests/*/*/*.cmo
echo ""
echo "///////////////////////////////////////////////////////"
echo "Nombre de tests qui passent : $nbTestsQuiPasse/$nbTests"
echo "///////////////////////////////////////////////////////"


