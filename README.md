# FileConverter
## 기능
- IRIS Studio에서 Data download시에 현재는 .csv 파일만 제공합니다. 
- 이 모듈을 통해 IRIS Studio의 Json 형식의 데이터를 .xlsx 파일로 변환하여 다운로드 할 수 있습니다.

## API

### POST /api/convert/excel
- Http Body에 Data Json을 담아 요청합니다.
- .xlsx 파일을 저장 후 해당 파일을 식별할 수 있는 Key를 반환합니다.
- 이 데이터는 IRIS Data JSON 형식에 맞게 구성되어있습니다.

### GET /api/download/excel?key={key}
- 위에서 받은 key를 요청하면, 서버에서 해당 파일을 찾아서 반환합니다.
