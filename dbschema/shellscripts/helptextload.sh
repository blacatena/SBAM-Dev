if [ "$#" -lt 1 ]
then
	echo "Specify the location of the help text file."
	exit 1
fi

helpTextHtml="$1"
if [ ! -f "$helpTextHtml" ]
then
	echo "$helpTextHtml not found."
	exit 2
fi

java HelpTextLoader "$helpTextHtml"