# Dependency Injecton
> @Component - аннотация, которой помечается класс-компонент. Необходим для внедрения зависимости. По умолчанию создаются Singleton экземпляры.

> @Service / @Controller - аннотации, содержащие в себе @Component. Нужны для повышения читабельности кода.

> @NotSingleton - аннотация, которая при вызове компонента создает новый экземпляр.

> @Dependent - данной аннотацией помечается зависимое поле. В аргументе указывается класс, от которого поле зависит.


> @Configuration - обязательная аннотация при создании конфигурационного класса, которой помечается он сам.

> @ComponentScan - обязательная аннотация при создании конфигурационного класса, в аргументе указывается путь к папке с компонентами.

> @Constructor - аннотация для объявления метода-конструктора, по паттерну которого будет создан экземпляр. Для каждого отдельновзятого компонента может быть реализован только один метод
