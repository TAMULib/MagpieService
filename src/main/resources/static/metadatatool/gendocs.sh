#!/bin/bash
# Generates test documents
if [ $# -eq 0 ]; then
	echo "Usage: gendocs [project name] [number of files created] [media type: txt, pdf, jpg]"
else

	if [  "$#" -eq 3  ]; then
		declare -a MEDIA_TYPES=(${3//,/ })
	else
		declare -a MEDIA_TYPES=('all')
	fi

	TILL="$2"
	echo $TILL
	COUNT=0

	mkdir "projects/"$1
	sleep 15

	while [ $COUNT -lt $TILL ]; do
		mkdir "projects/"$1"/"$1"_"$COUNT

		if [[ " ${MEDIA_TYPES[@]} " =~ " txt " ]] || [[ " ${MEDIA_TYPES[@]} " =~ " all " ]]; then
			echo "Hello, World! This is test "$1" "$COUNT"." > "projects/"$1"/"$1"_"$COUNT"/"$1"_"$COUNT".pdf.txt"
		fi

		if [[ " ${MEDIA_TYPES[@]} " =~ " pdf " ]] || [[ " ${MEDIA_TYPES[@]} " =~ " all " ]]; then
			cp ./sample-media/sample.pdf "projects/"$1"/"$1"_"$COUNT"/"$1"_"$COUNT".pdf"
		fi

		if [[ " ${MEDIA_TYPES[@]} " =~ " jpg " ]] || [[ " ${MEDIA_TYPES[@]} " =~ " all " ]]; then
			cp ./sample-media/sample.jpg "projects/"$1"/"$1"_"$COUNT"/"$1"_"$COUNT".jpg"
		fi

		let COUNT=COUNT+1

		if [ $(($COUNT%50)) == 0 ]; then
			sleep 15
		fi
	done
fi
