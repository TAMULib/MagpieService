#/bin/bash
if [ $# -eq 0 ]
then
	echo "Usage: gendocs [number of files created]"
else
	TILL="$1"
	echo $TILL
	COUNT=0
	while [ $COUNT -lt $TILL ]; 
	do
        	echo "Hello, World! This is test dissertation "$COUNT"." > "dissertation_"$COUNT".txt"
        	let COUNT=COUNT+1
	done
fi
#eof
