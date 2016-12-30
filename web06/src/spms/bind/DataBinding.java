package spms.bind;

public interface DataBinding {
	//PageController가 데이터가 필요한 경우 DataBinding 구현

	Object[] getDataBinders();
	//리턴되는 데이터 타입과 값 들이 pageController마다 다르기 때문에 Object타입으로 리턴한다.
	//getDataBinders() 의 반환값은 데이터의 이름과 타입정보를 담은 Object의 배열
}