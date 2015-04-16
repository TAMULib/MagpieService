#/bin/bash
#Generates test documents
if [ $# -eq 0 ]
then
	echo "Usage: gendocs [project name] [number of files created]"
else
	TILL="$2"
	echo $TILL
	COUNT=0
	
	mkdir $1
	sleep 30

	while [ $COUNT -lt $TILL ]; 
	do
    	mkdir $1"/"$1"_"$COUNT
    	
    	echo "Hello, World! This is test "$1" "$COUNT"." > $1"/"$1"_"$COUNT"/"$1"_"$COUNT".txt"
    	cp ../sample.pdf $1"/"$1"_"$COUNT"/"$1"_"$COUNT".pdf"
    	let COUNT=COUNT+1
		
		if [ $(($COUNT%50)) == 0 ]; then
			sleep 10
		fi 
	done
fi
#eof
