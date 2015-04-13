#/bin/bash
#Generates test documents
if [ $# -eq 0 ]
then
	echo "Usage: gendocs [number of files created]"
else
	TILL="$1"
	echo $TILL
	COUNT=0
	while [ $COUNT -lt $TILL ]; 
	do
    	mkdir "dissertation_"$COUNT
    	
    	echo "Hello, World! This is test dissertation "$COUNT"." > "dissertation_"$COUNT"/dissertation_"$COUNT".txt"
    	cp ../sample.pdf "dissertation_"$COUNT"/dissertation_"$COUNT".pdf"
    	let COUNT=COUNT+1
		
		if [ $(($COUNT%50)) == 0 ]; then
			sleep 10
		fi 
	done
fi
#eof
