#!/bin/bash
# Generates test documents
if [ $# -eq 0 ]; then
	echo "Usage: gendocs [project name] [number of documents] [number of files/doc] [media type: txt, pdf, jpg, tif]"
else

	if [  "$#" -eq 3  ]; then
		declare -a MEDIA_TYPES=(${4//,/ })
	else
		declare -a MEDIA_TYPES=('all')
	fi

	DOCS="$2"
	FILES="$3"
	echo $DOCS $FILES
	let DOCCOUNT=0
	let FILECOUNT=0

	mkdir "projects/"$1
	sleep 15

	while [[ $DOCCOUNT -lt $DOCS ]]; do
			
		mkdir "projects/"$1"/"$1"_"$DOCCOUNT
		
		while [[ $FILECOUNT -lt $FILES ]]; do
		
			echo $FILECOUNT $FILES
			
			if [[ " ${MEDIA_TYPES[@]} " =~ " txt " ]] || [[ " ${MEDIA_TYPES[@]} " =~ " all " ]]; then
				echo "Hello, World! This is test "$1" "$DOCCOUNT"." > "projects/"$1"/"$1"_"$DOCCOUNT"/"$1"_"$DOCCOUNT"_"$FILECOUNT".pdf.txt"
			fi
	
			if [[ " ${MEDIA_TYPES[@]} " =~ " pdf " ]] || [[ " ${MEDIA_TYPES[@]} " =~ " all " ]]; then
				cp ./sample-media/sample.pdf "projects/"$1"/"$1"_"$DOCCOUNT"/"$1"_"$DOCCOUNT"_"$FILECOUNT".pdf"
			fi
			
			if [[ " ${MEDIA_TYPES[@]} " =~ " jpg " ]] || [[ " ${MEDIA_TYPES[@]} " =~ " all " ]]; then
				cp ./sample-media/sample.jpg "projects/"$1"/"$1"_"$DOCCOUNT"/"$1"_"$DOCCOUNT"_"$FILECOUNT".jpg"
			fi
			
			if [[ " ${MEDIA_TYPES[@]} " =~ " tif " ]] || [[ " ${MEDIA_TYPES[@]} " =~ " all " ]]; then
				cp ./sample-media/sample.tif "projects/"$1"/"$1"_"$DOCCOUNT"/"$1"_"$DOCCOUNT"_"$FILECOUNT".tif";
				echo "projects/"$1"/"$1"_"$DOCCOUNT"/"$1"_"$DOCCOUNT"_"$FILECOUNT".tif"
			fi
			
			let FILECOUNT++
			
		done
		
		let FILECOUNT=0
		
		let DOCCOUNT++
		
		if [ $(($DOCCOUNT%50)) == 0 ]; then
			sleep 15
		fi
		
	done
fi
