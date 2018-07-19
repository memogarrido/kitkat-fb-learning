const functions = require('firebase-functions');
const vision = require('@google-cloud/vision');
const client = new vision.ImageAnnotatorClient();
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//

exports.createMap = functions.storage.object().onFinalize((object) => {

    console.log('nombre del objeto', object.name);
    
    var db = admin.firestore();

    var idArtist = object.name.replace('celebrity/', '');
    idArtist = idArtist.replace('.jpg', '');
    idArtist = idArtist.replace('.gif', '');

    console.log('idArtist ', idArtist);

    console.log(`gs://${object.bucket}/${object.name}`);
    return client.faceDetection(`gs://${object.bucket}/${object.name}`)
        .then(results => {

            
            console.log('xxxxxxxxx ', results[0]);
            const faces = results[0].faceAnnotations;
            console.log('yyyyy ', faces);
            faces.forEach((face, i) => {
                console.log('zzzz ', face);
                var leftEye = face.landmarks[0].position;
                var rightEye = face.landmarks[1].position;
                var noseTip = face.landmarks[7].position;
                var mouthLeft = face.landmarks[10].position;
                var mouthRight = face.landmarks[11].position;

                console.log("poligono ", face.boundingPoly);

                var referencePoint = face.boundingPoly.vertices[0];
                var reference = parseFloat(getDistance(noseTip, referencePoint));

                console.log("referencia ...", reference);

                var leftEyeDistanceValue = parseFloat(getDistanceRatio(leftEye, noseTip, reference));
                var rightEyeDistanceValue = parseFloat(getDistanceRatio(noseTip, rightEye, reference));
                var leftMouthDistanceValue = parseFloat(getDistanceRatio(noseTip, mouthLeft, reference));
                var rightMouthDistanceValue = parseFloat(getDistanceRatio(noseTip, mouthRight, reference));

                

                var data = { faceMap: { leftMouthDistance: leftMouthDistanceValue, rightEyeDistance: rightEyeDistanceValue, leftEyeDistance: leftEyeDistanceValue, rightMouthDistance: rightMouthDistanceValue } };


                

                var artistsRef = db.collection('artists');

                var updateObject = JSON.stringify(data);
                var artisSelected = artistsRef.doc(idArtist).update(data)
                    .catch(err => {
                        console.log('Error getting documents', err);
                    });

            });
        })
        .catch(err => {
            console.error('ERROR:', err);
        });

});



exports.whoAmIAlike = functions.https.onRequest((req, res) => {
    var leftEyeDistance = req.query.leftEyeDistance;
    var rightEyeDistance = req.query.rightEyeDistance;
    var leftMouthDistance = req.query.leftMouthDistance;
    var rightMouthDistance = req.query.rightMouthDistance;

    var db = admin.firestore();
    var artistsRef = db.collection('artists').get()
    .then(snapshot => {
        snapshot.forEach(doc => {
          console.log('facemap ', doc.data().faceMap);
          var fcMap = doc.data().faceMap;

          console.log('facemap field', fcMap.rightEyeDistance, "keys");

           var leftEyeDifference = (leftEyeDistance * 100 )/fcMap.leftEyeDistance;
           var rightEyeDifference = (rightEyeDistance * 100 )/fcMap.rightEyeDistance;
           var leftMouthDifference = (leftMouthDistance * 100 )/fcMap.leftMouthDistance;
           var rightMouthEyeDifference = (rightMouthDistance * 100 )/fcMap.rightMouthDistance;

           var differenceAverage = (leftEyeDifference+rightEyeDifference+leftMouthDifference+rightMouthEyeDifference)/4
          
           console.log(doc.data().name, '...',  differenceAverage);
        });


        var result = JSON.parse('{ "name":"John", "age":30, "city":"New York"}')

        var resultString = JSON.stringify(result);
        console.log('result string', resultString);
        res.send(resultString.toString());
    
        return;

      })
      .catch(err => {
        console.log('Error getting documents', err);
      });

      
    

    
});


function getDistance(startPoint, endPoint) {

    return parseFloat(Math.sqrt(Math.pow((endPoint.x - startPoint.x), 2) +
        Math.pow((endPoint.y - startPoint.y), 2)));
}

function getDistanceRatio(startPoint, endPoint, reference) {
    var distance = getDistance(startPoint, endPoint);
    return parseFloat(distance / reference * 100);
}

/* exports.updateUser = functions.firestore
    .document('matches/{matchId}/votes').collection('votes')
    .onCreate((change, context) => {
      // Get an object representing the document
      // e.g. {'name': 'Marie', 'age': 66}
      const newValue = change.after.data();


      // ...or the previous value before this update
      const previousValue = change.before.data();


      console.log(change.after,' .... after');
      console.log(previousValue,' .... previousValue');
      console.log(newValue,' .... newValue');

      // perform desired operations ...
    }); */


