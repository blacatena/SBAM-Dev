echo "***********************************"
echo "*** SBAM Aunthentication Script ***"
echo "***********************************"

if [ "$#" -lt 1 ]
then
	echo "usage: authgen.sh [options]"
	echo
	echo "-silent    To run without output (default)."
	echo "-verbose   To run with specific progress messages."
	echo "-ucn       To generate the export with UCNs."
	echo "-legacy    To generate the export with legacy (Global) customer codes."
	echo "n          Where n is any positive, non-zero number, to output agreement, site and method counts after every n processed agreements."
	echo "-debug     Speeds processing by restricting the run to 500 agreements."
	echo "yyyy-mm-dd Date as of which to execute this generation.  USE WITH CAUTION AND FULL KNOWLEDGE OF WHAT YOU'RE DOING!!!"
	echo "-help      To show this help list."
	
	exit 1
fi

java AuthenticationGenerator "$*"

echo "*******************"
echo "Execution complete."
echo "*******************"